using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using MagicSoftware.Common.Controls.Utils;
using MagicSoftware.Integration.UserComponents;

namespace ISO8583
{

    public class MyData : PropertyChangedBase // defines properties to save control value of UI
    {
        private Alpha _resourceName;
        private Alpha _operation;
        private Alpha _operationWithSplitCharacter;
        private Alpha _entityObject;
        private string _operationString;
        private string _entityObjectString;

        [In]
        [PrimitiveDataTypes(DataType.Alpha)]
        [Encoding(EncodingType.Unicode)]
        [DisplayPropertyName("ResourceName")]
        public Alpha ResourceName
        {
            get { return _resourceName; }
            set
            {
                _resourceName = value;
                OnPropertyChanged("ResourceName");
            }
        }
        public bool IsDirty { get; set; }


        private List<MethodEntity> _methodEntityList;
        public List<MethodEntity> MethodEntityList
        {
            get { return _methodEntityList; }
            set { _methodEntityList = value; }
        }

        [In]
        [PrimitiveDataTypes(DataType.Alpha)]
        [Encoding(EncodingType.Unicode)]
        [DisplayPropertyName("EntityObject")]
        public Alpha EntityObject
        {
            get { return _entityObject; }
            set
            {
                _entityObject = value;
                OnPropertyChanged("EntityObject");
            }
        }

        public string EntityObjectString
        {
            get { return _entityObjectString; }
            set
            {
                if (!string.IsNullOrEmpty(value))
                {
                    _entityObjectString = value;
                    EntityObject.SetAlpha(_entityObjectString);
                    OnPropertyChanged("EntityObjectString");
                }
            }
        }

        [In]
        [PrimitiveDataTypes(DataType.Alpha)]
        [Encoding(EncodingType.Unicode)]
        [DisplayPropertyName("Operation")]
        public Alpha Operation
        {
            get { return _operation; }
            set
            {
                _operation = value;
                OnPropertyChanged("Operation");
            }
        }

        private ObservableCollection<string> _operationList;

        public ObservableCollection<string> OperationList
        {
            get
            {
                return _operationList;
            }
            set
            {
                _operationList = value;
                OnPropertyChanged("OperationList");
            }
        }

        public string OperationString
        {
            get { return _operationString; }
            set
            {
                if (!string.IsNullOrEmpty(value))
                {
                    _operationString = value;
                    Operation.SetAlpha(_operationString);
                    OnPropertyChanged("OperationString");
                }
            }
        }


        [In]
        [PrimitiveDataTypes(DataType.Alpha)]
        [Encoding(EncodingType.Unicode)]
        [DisplayPropertyName("OperationWithSplitCharacter")]
        public Alpha OperationWithSplitCharacter
        {
            get { return _operationWithSplitCharacter; }
            set
            {
                _operationWithSplitCharacter = value;
                OnPropertyChanged("OperationWithSplitCharacter");
            }
        }

        [PrimitiveDataTypes(DataType.Logical)]
        [DisplayPropertyName("Operation Success")]
        [AllowEmptyExpression]
        [Out]
        public Variable OperationSuccess { get; set; }



        [PrimitiveDataTypes(DataType.Alpha)]
        [Out]
        [DisplayPropertyName("Store Result")]
        [AllowEmptyExpression]
        public Expression StoreResultFile { get; set; }


        [PrimitiveDataTypes(DataType.Alpha)]
        [Out]
        [DisplayPropertyName("Store Result")]
        [AllowEmptyExpression]
        public Expression StoreSplitFile { get; set; }


        [PrimitiveDataTypes(DataType.Blob)]
        [Encoding(EncodingType.Unicode)]
        [Out]
        [DisplayPropertyName("Store Result")]
        [AllowEmptyExpression]
        public Variable StoreResultVar { get; set; }

        [In]
        [PrimitiveDataTypes(DataType.Alpha)]
        [Encoding(EncodingType.Unicode)]
        [DisplayPropertyName("Directory")]
        [AllowEmptyExpression]
        public Expression Directory { get; set; }

        [In]
        [PrimitiveDataTypes(DataType.Alpha)]
        [Encoding(EncodingType.Unicode)]
        [DisplayPropertyName("Prefix")]
        [AllowEmptyExpression]
        public Expression Prefix { get; set; }

        [In]
        [PrimitiveDataTypes(DataType.Numeric)]
        [Encoding(EncodingType.Unicode)]
        [DisplayPropertyName("RecordsPerFile")]
        [AllowEmptyExpression]
        public Expression RecordsPerFile { get; set; }


        [PrimitiveDataTypes(DataType.Numeric)]
        [Out]
        [DisplayPropertyName("NumberOfSplit")]
        [AllowEmptyExpression]
        public Variable NumberOfSplit { get; set; }

        public MyData()
        {
            MethodEntityList = new List<MethodEntity>();

            EntityObject = new Alpha();
            EntityObject.SetAlpha(string.Empty);
            EntityObjectString = string.Empty;

            OperationSuccess = new Variable();
            OperationSuccess.SetValue("");

            StoreResultVar = new Variable();
            StoreResultVar.SetValue(string.Empty);

            StoreResultFile = new Expression();
            StoreResultFile.SetValue(string.Empty);

            StoreSplitFile = new Expression();
            StoreSplitFile.SetValue(string.Empty);

            Operation = new Alpha();
            OperationString = string.Empty;
            OperationList = new ObservableCollection<string>();

            OperationWithSplitCharacter = new Alpha();
            OperationWithSplitCharacter.SetAlpha(string.Empty);

            ResourceName = new Alpha();
            Directory = new Expression();
            Prefix = new Expression();
            RecordsPerFile = new Expression();

            NumberOfSplit = new Variable();
        }
        public override void OnPropertyChanged(string propertyName)
        {
            IsDirty = true;
            base.OnPropertyChanged(propertyName);
        }
    }
}
