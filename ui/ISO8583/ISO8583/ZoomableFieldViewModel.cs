using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using MagicSoftware.iBolt.Common.Controls;
using MagicSoftware.Integration.UserComponents;
using MagicSoftware.Integration.UserComponents.Interfaces;
using MgExpression = MagicSoftware.Integration.UserComponents.Expression;

namespace ISO8583
{
    public interface IPropertyAccessor : IValueEditor
    {
        bool IsExpression { get; }

        object GetValue();

        void SetValue(object value);
    }

    public interface IValueEditor
    {
        object OpenEditor(object localValue, ISDKStudioUtils utils, DataType datatype);
    }

    public class ZoomableFieldViewModel : INotifyPropertyChanged
    {
        private readonly PropertyInfo propertyInfo;
        private object localValue;
        private IPropertyAccessor propertyAccessor;
        public string ExpressionEditorPropertyName { get; set; }
        private Action<ZoomableFieldViewModel> propertyChangedCallback;

        public object Value
        {
            get { return localValue; }
            set
            {
                if (!Equals(localValue, value))
                {
                    localValue = value;
                    propertyChangedCallback(this);
                    OnPropertyChanged(nameof(Value));
                }
            }
        }

        protected object WorkData { get; private set; }

        public ZoomableFieldViewModel(IPropertyAccessor propertyAccessor, Action<ZoomableFieldViewModel> propertyChangedCallback)
        {
            this.propertyAccessor = propertyAccessor;
            ResetLocalValue();
            this.propertyChangedCallback = propertyChangedCallback;
        }

        public virtual void CommitValue()
        {
            propertyAccessor.SetValue(localValue);
        }

        public void OpenExpressionEditor(ISDKStudioUtils utils, DataType datatype)
        {
            if (propertyAccessor.IsExpression)
            {
                var expression = new MgExpression();
                expression.SetValue((string)Value);
                var newExpression = utils.OpenExpressionEditor(expression, datatype, ExpressionEditorPropertyName);
                Value = newExpression.GetValue();
            }
        }

        public void OpenFieldEditor(ISDKStudioUtils utils, DataType datatype)
        {
            Value = propertyAccessor.OpenEditor(Value, utils, datatype);
        }

        public void OpenHeaderEditor(ISDKStudioUtils utils, DataType datatype)
        {
            Value = propertyAccessor.OpenEditor(Value, utils, datatype);
        }

        protected object GetPropertyValue()
        {
            return propertyInfo.GetValue(WorkData, null);
        }

        protected void ResetLocalValue()
        {
            localValue = propertyAccessor.GetValue();
        }

        public event PropertyChangedEventHandler PropertyChanged;
        protected virtual void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }


    public class Option : Tuple<string, ZoomableFieldViewModel, string>
    {
        public Option(string id, ZoomableFieldViewModel vm, string displayValue) :
            base(id, vm, displayValue)
        {

        }
    }

    abstract class ZoomableOptionsProvider
    {
        public IEnumerable<Option> Options { get; private set; }

        public void CreateOptions(MyData propertiesOwner, Action<ZoomableFieldViewModel> propertyChangedCallback)
        {
            var options = new List<Option>();

            AddOptions(options, propertiesOwner, propertyChangedCallback);

            Options = options;
        }

        internal void Commit()
        {
            foreach (var tuple in Options)
            {
                tuple.Item2?.CommitValue();
            }
        }

        protected abstract void AddOptions(List<Option> options,
            MyData propertiesOwner, Action<ZoomableFieldViewModel> propertyChangedCallback);
    }


    internal abstract class PropertyAccessorBase<TProperty> : IPropertyAccessor
    {
        private readonly Func<TProperty> getPropertyValue;
        private readonly Action<TProperty> setPropertyValue;

        public virtual bool IsExpression => false;

        protected PropertyAccessorBase(Func<TProperty> getPropertyValue, Action<TProperty> setPropertyValue)
        {
            this.getPropertyValue = getPropertyValue;
            this.setPropertyValue = setPropertyValue;
        }

        public virtual object GetValue()
        {
            return getPropertyValue();
        }

        public virtual void SetValue(object value)
        {
            setPropertyValue((TProperty)value);
        }

        public abstract object OpenEditor(object localValue, ISDKStudioUtils utils, DataType datatype);
    }


    internal class VariablePropertyAccessor : PropertyAccessorBase<Variable>
    {
        public VariablePropertyAccessor(Func<Variable> getPropertyValue)
            : base(getPropertyValue, (v) => { })
        {
        }

        public override object GetValue()
        {
            var variable = (Variable)base.GetValue();
            return variable.GetValue();
        }

        public override void SetValue(object value)
        {
            var variable = (Variable)base.GetValue();
            variable.SetValue((string)value);
        }

        public override object OpenEditor(object value, ISDKStudioUtils utils, DataType datatype)
        {
            var currentValue = new Variable();
            currentValue.SetValue((string)value);
            var newValue = utils.OpenVariablePicklist(currentValue, VariableFilter.ALLVariables, datatype);
            return newValue.GetValue();
        }
    }

    internal class SuccessOptions : ZoomableOptionsProvider
    {
        protected override void AddOptions(List<Option> options, MyData propertiesOwner, Action<ZoomableFieldViewModel> propertyChangedCallback)
        {
            var varAccessor = new VariablePropertyAccessor(() => propertiesOwner.OperationSuccess);
            var varFieldVM = new ZoomableFieldViewModel(varAccessor, propertyChangedCallback);

            options.Add(new Option("None", null, "None"));
            options.Add(new Option("Variable", varFieldVM, "Variable"));
        }
    }

    internal class StorageOptions : ZoomableOptionsProvider
    {
        protected override void AddOptions(List<Option> options, MyData propertiesOwner, Action<ZoomableFieldViewModel> propertyChangedCallback)
        {
            var varAccessor = new VariablePropertyAccessor(() => propertiesOwner.StoreResultVar);
            var varFieldVM = new ZoomableFieldViewModel(varAccessor, propertyChangedCallback);

            var resultFileAccessor = new ExpressionPropertyAccessor(() => propertiesOwner.StoreResultFile, new FileFieldValueEditor());
            var fileFieldVM = new ZoomableFieldViewModel(resultFileAccessor, propertyChangedCallback);

            var varSplitFileAccessor = new ExpressionPropertyAccessor(() => propertiesOwner.StoreSplitFile, new FileFieldValueEditor());
            var varSplitFile = new ZoomableFieldViewModel(varSplitFileAccessor, propertyChangedCallback);


            options.Add(new Option("Variable", varFieldVM, "Variable"));
            options.Add(new Option("File", fileFieldVM, "File"));
            options.Add(new Option("SplitFile", varSplitFile, "Split File"));
        }
    }

    internal class ExpressionPropertyAccessor : PropertyAccessorBase<Expression>
    {
        private IValueEditor valueEditor;

        public override bool IsExpression => true;

        public ExpressionPropertyAccessor(Func<Expression> getPropertyValue, IValueEditor valueEditor)
            : base(getPropertyValue, (e) => { })
        {
            this.valueEditor = valueEditor;
        }

        public override object GetValue()
        {
            var expression = (Expression)base.GetValue();
            return expression.GetValue();
        }

        public override void SetValue(object value)
        {
            var expression = (Expression)base.GetValue();
            expression.SetValue((string)value);
        }

        public override object OpenEditor(object localValue, ISDKStudioUtils utils, DataType datatype)
        {
            return valueEditor?.OpenEditor(localValue, utils, datatype);
        }
    }

    internal class FileFieldValueEditor : IValueEditor
    {
        public IList<string> FileFilters { get; set; }

        public string FileSelectionHeader { get; set; }

        public FileFieldValueEditor()
        {
            FileSelectionHeader = "Select a file";
            FileFilters = new List<string>();
            FileFilters.Add("All Files (*.*)|*.*");
        }

        public object OpenEditor(object localValue, ISDKStudioUtils utils, DataType datatype)
        {
            string newFilePath;
            if (utils.OpenFileSelectionDailog(FileSelectionHeader, FileFilters.Aggregate((l, r) => l + "|" + r), (string)localValue, out newFilePath))
                return newFilePath;

            return localValue;
        }
    }

    internal class DirectoryValueEditor : IValueEditor
    {
        public DirectoryValueEditor()
        {
        }

        public object OpenEditor(object localValue, ISDKStudioUtils utils, DataType datatype)
        {
            return OpenEditor(localValue, utils, DataType.Alpha);
        }
    }

    internal class PrefixValueEditor : IValueEditor
    {
        public PrefixValueEditor()
        {
        }

        public object OpenEditor(object localValue, ISDKStudioUtils utils, DataType datatype)
        {
            return OpenEditor(localValue, utils, DataType.Alpha);
        }
    }

    internal class RecordsPerFileValueEditor : IValueEditor
    {
        public RecordsPerFileValueEditor()
        {
        }

        public object OpenEditor(object localValue, ISDKStudioUtils utils, DataType datatype)
        {
            return OpenEditor(localValue, utils, DataType.Alpha);
        }
    }

    public class EntitiesPicklistDefinition : IPickListProvider
    {

        public List<PickListColumnDefinition> PickListColumns
        {
            get
            {
                return new List<PickListColumnDefinition>
                {
                    new PickListColumnDefinition("Entity", "Property1", true),
                };
            }
        }

        public string PickListWindowTitle
        {
            get { return "Entity Object List"; }
        }
    }
}
