using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Magicsoftware.iBolt.Common.Controls;
using MagicSoftware.Common.Controls.Utils;
using MagicSoftware.iBolt.Common;
using MagicSoftware.iBolt.Common.Controls;
using MagicSoftware.Integration.UserComponents;
using MagicSoftware.Integration.UserComponents.Interfaces;

namespace ISO8583
{
    public class ISO8583ViewModel : PropertyChangedBase
    {
        public MyData StepModel { get; set; }
        private readonly ISDKStudioUtils _studioUtils;
        private IReadOnlyResourceConfiguration _resourceData;
        private IComponentServicesProvider _servicesProvider;
        private string _variable = "Variable";
        private string _file = "File";
        private string _splitFile = "SplitFile";
        private string _none = "None";
        private ZoomableFieldViewModel _directory;
        private ZoomableFieldViewModel _prefix;
        private ZoomableFieldViewModel _recordsPerFile;
        private ZoomableFieldViewModel _numberOfSplit;

        #region Output

        private readonly StorageOptions resultStorageOptions = new StorageOptions();
        private readonly SuccessOptions operationSuccessOptions = new SuccessOptions();

        private Option resultStorage;
        private Option operationSuccess;

        public ZoomableFieldViewModel ResultStorage => resultStorage.Item2;

        public Option ResultStorageOption
        {
            get { return resultStorage; }
            set
            {
                if (Equals(resultStorage, value))
                    return;

                // Clear the value of the other option.
                if (resultStorage != null)
                    resultStorage.Item2.Value = null;

                resultStorage = value;

                if (resultStorage.Item1 == _variable && resultStorage.Item2 != null
                    && string.IsNullOrEmpty(resultStorage.Item2.Value?.ToString()))
                {
                    resultStorage.Item2.Value = "C.UserBlob";
                    OnPropertyChanged(nameof(ResultStorage));
                }

                if (resultStorage.Item1 == _splitFile && resultStorage.Item2 != null
                   && string.IsNullOrEmpty(resultStorage.Item2.Value?.ToString()))
                {
                    resultStorage.Item2.Value = _splitFile;
                    OnPropertyChanged(nameof(ResultStorage));
                }

                OnPropertyChanged(nameof(ResultStorageOption));
                OnPropertyChanged(nameof(ResultStorage));
            }
        }

        public IEnumerable<Option> ResultStorageOptions => resultStorageOptions.Options;

        public Option OperationSuccessOption
        {
            get { return operationSuccess; }
            set
            {
                if (Equals(operationSuccess, value))
                    return;

                if (operationSuccess?.Item2 != null)
                    operationSuccess.Item2.Value = null;

                operationSuccess = value;
                OnPropertyChanged(() => OperationSuccessOption);
                OnPropertyChanged(() => OperationSuccess);
            }
        }

        public ZoomableFieldViewModel Directory
        {
            get { return _directory; }
            set
            {
                _directory = value;
                OnPropertyChanged(() => Directory);
            }
        }

        public ZoomableFieldViewModel Prefix
        {
            get { return _prefix; }
            set
            {
                _prefix = value;
                OnPropertyChanged(() => Prefix);
            }
        }

        public ZoomableFieldViewModel RecordsPerFile
        {
            get { return _recordsPerFile; }
            set
            {
                _recordsPerFile = value;
                OnPropertyChanged(() => RecordsPerFile);
            }
        }

        public ZoomableFieldViewModel NumberOfSplit
        {
            get { return _numberOfSplit; }
            set
            {
                _numberOfSplit = value;
                OnPropertyChanged(() => NumberOfSplit);
            }
        }

        public IEnumerable<Option> OperationSuccessOptions => operationSuccessOptions.Options;

        public ZoomableFieldViewModel OperationSuccess => operationSuccess.Item2;

        #endregion

        private Dictionary<string, IPickListProvider> picklistDefinition;

        public Dictionary<string, IPickListProvider> PicklistDefintion
        {
            get { return picklistDefinition; }
        }

        public ISO8583ViewModel(MyData stepModel, IReadOnlyResourceConfiguration resourceData,
            IComponentServicesProvider servicesProvider, ISDKStudioUtils studioUtils)
        {
            StepModel = stepModel;
            _servicesProvider = servicesProvider;
            _resourceData = resourceData;
            _studioUtils = studioUtils;
            SetConfiguration();
        }


        // Load Shopify Configuration.
        private void SetConfiguration()
        {
            if (StepModel == null)
                return;

            // Set Resource Name.
            if (_resourceData != null)
            {
                StepModel.ResourceName.SetAlpha(_resourceData.ResourceName);
            }

            // Load Methods list from file.
            LoadMethodsFromFile();

            StepModel.EntityObjectString = StepModel.EntityObject.GetAlpha();

            // Generate Operation Combo List.
            if (StepModel.OperationWithSplitCharacter != null &&
                !string.IsNullOrEmpty(StepModel.OperationWithSplitCharacter.GetAlpha()))
            {
                GenerateOperationList(StepModel.OperationWithSplitCharacter.GetAlpha());
            }

            if (StepModel.Operation != null && !string.IsNullOrEmpty(StepModel.Operation.GetAlpha()))
                StepModel.OperationString = StepModel.Operation.GetAlpha();


            picklistDefinition = new Dictionary<string, IPickListProvider>
            {
                {"Entities", new EntitiesPicklistDefinition()}
            };

            // Prepare the storage options list.
            resultStorageOptions.CreateOptions(StepModel, (z) => StepModel.IsDirty = true);
            ResultStorageOption = resultStorageOptions.Options.FirstOrDefault();

            operationSuccessOptions.CreateOptions(StepModel, (z) => StepModel.IsDirty = true);
            OperationSuccessOption = operationSuccessOptions.Options.FirstOrDefault();

            // Initialize the result storage field.
            string resultStorageType = string.Empty;

            if (!string.IsNullOrEmpty(StepModel.StoreResultVar.GetValue()) && string.IsNullOrEmpty(StepModel.StoreResultFile.GetValue()) &&
                string.IsNullOrEmpty(StepModel.StoreSplitFile.GetValue()))
            {
                resultStorageType = _variable;
            }
            else if (string.IsNullOrEmpty(StepModel.StoreResultVar.GetValue()) && !string.IsNullOrEmpty(StepModel.StoreResultFile.GetValue()) &&
                string.IsNullOrEmpty(StepModel.StoreSplitFile.GetValue()))
            {
                resultStorageType = _file;
            }

            else if (string.IsNullOrEmpty(StepModel.StoreResultVar.GetValue()) && string.IsNullOrEmpty(StepModel.StoreResultFile.GetValue()) &&
              !string.IsNullOrEmpty(StepModel.StoreSplitFile.GetValue()))
            {
                resultStorageType = _splitFile;
            }
            else
            {
                resultStorageType = _variable;
            }

            ResultStorageOption = resultStorageOptions.Options.First(t => t.Item1 == resultStorageType);

            if (resultStorageType == _variable)
            {
                if (resultStorage.Item2 != null && string.IsNullOrEmpty(resultStorage.Item2.Value?.ToString()))
                {
                    resultStorage.Item2.Value = "C.UserBlob";
                    OnPropertyChanged(nameof(ResultStorage));
                }
            }


            // Set the operation success field.
            var operationSuccessType = _none;
            if (!string.IsNullOrEmpty(StepModel.OperationSuccess.GetValue()))
                operationSuccessType = _variable;
            OperationSuccessOption = operationSuccessOptions.Options.First(t => t.Item1 == operationSuccessType);

            var varDirectoryInformationAccessor = new ExpressionPropertyAccessor(() => StepModel.Directory, new DirectoryValueEditor());
            Directory = new ZoomableFieldViewModel(varDirectoryInformationAccessor, (z) => StepModel.IsDirty = true)
            {
                Value = StepModel.Directory.GetValue()
            };

            var varPrefixInformationAccessor = new ExpressionPropertyAccessor(() => StepModel.Prefix, new PrefixValueEditor());
            Prefix = new ZoomableFieldViewModel(varPrefixInformationAccessor, (z) => StepModel.IsDirty = true)
            {
                Value = StepModel.Prefix.GetValue()
            };

            var varRecordsPerFileInformationAccessor = new ExpressionPropertyAccessor(() => StepModel.RecordsPerFile, new RecordsPerFileValueEditor());
            RecordsPerFile = new ZoomableFieldViewModel(varRecordsPerFileInformationAccessor, (z) => StepModel.IsDirty = true)
            {
                Value = StepModel.RecordsPerFile.GetValue()
            };


            var varOutputHeaderAccessor = new VariablePropertyAccessor(() => StepModel.NumberOfSplit);
            NumberOfSplit = new ZoomableFieldViewModel(varOutputHeaderAccessor, (z) => StepModel.IsDirty = true)
            {
                Value = StepModel.NumberOfSplit.GetValue()
            };
        }

        public bool IsDirty()
        {
            return StepModel != null && StepModel.IsDirty;
        }

        public void LoadMethodsFromSchemaIniFile()
        {
            string schemaFilePath = Path.Combine(_studioUtils.GetSystemProperty("ConnectorPath"), "schema", "schema.ini");

            if (string.IsNullOrEmpty(schemaFilePath))
                iBMessageBox.ErrorMessage("Please check schema.ini file and try again");
            else
            {
                using (StreamReader stramReader = File.OpenText(schemaFilePath.Trim()))
                {
                    string method;
                    while ((method = stramReader.ReadLine()) != null)
                    {
                        StepModel.OperationList.Add(method);
                    }
                }
            }
        }

        internal void EditExpression(string commandParameter)
        {
            switch (commandParameter)
            {
                case "storage":
                    ResultStorage.ExpressionEditorPropertyName = "Store Result In";
                    ResultStorage.OpenExpressionEditor(_studioUtils, DataType.Alpha);
                    break;
                case "directory":
                    Directory.ExpressionEditorPropertyName = "Directory";
                    Directory.OpenExpressionEditor(_studioUtils, DataType.Alpha);
                    break;
                case "prefix":
                    Prefix.ExpressionEditorPropertyName = "Prefix";
                    Prefix.OpenExpressionEditor(_studioUtils, DataType.Alpha);
                    break;
                case "recordsPerFile":
                    RecordsPerFile.ExpressionEditorPropertyName = "Records Per File";
                    RecordsPerFile.OpenExpressionEditor(_studioUtils, DataType.Numeric);
                    break;
            }
        }

        internal void Prompt(string commandParameter)
        {
            switch (commandParameter)
            {
                case "storage":
                    ResultStorage.OpenFieldEditor(_studioUtils, DataType.Blob);
                    break;
                case "success":
                    OperationSuccess.OpenFieldEditor(_studioUtils, DataType.Logical);
                    break;
                case "numberOfSplits":
                    NumberOfSplit.OpenFieldEditor(_studioUtils, DataType.Numeric);
                    break;
            }
        }

        public void LoadMethodsFromFile()
        {
            var schemaFilePath = Path.Combine(_studioUtils.GetSystemProperty("ConnectorPath"), "schema", "MethodList.xml");
            if (string.IsNullOrEmpty(schemaFilePath))
                iBMessageBox.ErrorMessage("Please check MethodList.xml file and try again");
            else
            {
                if (File.Exists(schemaFilePath))
                {
                    try
                    {
                        var methodList = Utils.FromXml<MethodList>(schemaFilePath);
                        StepModel.MethodEntityList = methodList.MethodEntityList;
                    }
                    catch (Exception ex)
                    {
                        iBMessageBox.ErrorMessage(ex.Message + Environment.NewLine + ex.StackTrace);
                    }
                }
            }
        }

        public void ShowEntityObject()
        {
            try
            {
                if (StepModel.MethodEntityList.Count > 0)
                {
                    PickListControl.SortDirection = true;

                    PickListControl entitiesPicklist = new PickListControl(Convert2ComboList(StepModel.MethodEntityList),
                                                       PicklistDefintion["Entities"], StepModel.EntityObjectString, false, true);
                    entitiesPicklist.ShowDialog();
                    if (entitiesPicklist.CloseResult == DialogCloseResult.Selected)
                    {
                        if (entitiesPicklist.SelectedItem != null)
                        {
                            StepModel.EntityObjectString = entitiesPicklist.SelectedItem.Property1;
                            if (entitiesPicklist.SelectedItem.SelectedValue != null)
                                GenerateOperationList(entitiesPicklist.SelectedItem.SelectedValue.ToString());
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                iBMessageBox.ErrorMessage(ex.Message + Environment.NewLine + ex.StackTrace);
            }
        }

        private void GenerateOperationList(string operationListWithSplitCharacter)
        {
            StepModel.OperationWithSplitCharacter.SetAlpha(operationListWithSplitCharacter);
            StepModel.OperationList.Clear();

            var tmpoperationList = operationListWithSplitCharacter.Split('|');

            foreach (var operation in tmpoperationList)
            {
                StepModel.OperationList.Add(operation);
            }

            if (StepModel.Operation != null && string.IsNullOrEmpty(StepModel.Operation.GetAlpha()) && StepModel.OperationList.Count > 0)
                StepModel.OperationString = StepModel.OperationList[0];
        }

        private ObservableCollection<ComboItem> Convert2ComboList(List<MethodEntity> entitiesList)
        {
            ObservableCollection<ComboItem> comboitems = new ObservableCollection<ComboItem>();

            foreach (var item in entitiesList)
            {
                comboitems.Add(new ComboItem() { DisplayValue = item.Name, Property1 = item.Name, SelectedValue = item.OperationList, Description = item.Name, ToolTip = item.Name });
            }
            return comboitems;

        }
        public void CommitChanges()
        {
            resultStorageOptions.Commit();
            operationSuccessOptions.Commit();

            if (Directory.Value != null)
                StepModel.Directory.SetValue(Directory.Value.ToString());

            if (Prefix.Value != null)
                StepModel.Prefix.SetValue(Prefix.Value.ToString());

            if (RecordsPerFile.Value != null)
                StepModel.RecordsPerFile.SetValue(RecordsPerFile.Value.ToString());

            if (NumberOfSplit.Value != null)
                StepModel.NumberOfSplit.SetValue(NumberOfSplit.Value.ToString());
        }
    }
}
