
using System;
using System.Collections.Generic;
using MagicSoftware.Integration.UserComponents.Interfaces;
using MagicSoftware.Integration.UserComponents;
using System.ComponentModel.Composition;
using System.Windows.Forms;
using System.Collections;
using System.IO;
using System.Windows;
using Magicsoftware.iBolt.Common.Controls.Windows;
using MagicSoftware.iBolt.Common;

namespace ISO8583
{

    [Export(typeof(IUserComponent))]
    public class UI : IUserComponent
    {
        private bool res = false;
        private ISDKStudioUtils utils;
        MyData _adaptorData = new MyData();
        public ArrayList SchemaList = new ArrayList();

        private ISO8583ViewModel _eParcelAustraliaViewModel;

        [Import]
        private IComponentServicesProvider servicesProvider;

        public UI()
        {
        }

        #region IUserComponent implementation

        public object CreateDataObject()
        {
            return new MyData();
        }

        public bool? Configure(ref object dataObject, ISDKStudioUtils utils, IReadOnlyResourceConfiguration resourceData, object navigateTo, out bool configurationChanged)
        {
            bool isConfigExist = true;

            if (resourceData == null)
                throw new OperationCanceledException("A resource name was not selected. If you are using a dynamic resource, a default resource should be defined.");

            if (dataObject != null && string.IsNullOrEmpty(((MyData)dataObject).Operation.GetAlpha()))
            {
                isConfigExist = false;
            }

            _eParcelAustraliaViewModel = new ISO8583ViewModel((MyData)dataObject, resourceData, servicesProvider, utils);
            var stepView = new ISO8583StepView(_eParcelAustraliaViewModel, isConfigExist);

            StepWindowContainer container = new StepWindowContainer(stepView, new MapperConfigurationExitStrategy())
            {
                Owner = System.Windows.Application.Current.MainWindow
            };
            bool? windowResult = container.ShowDialog();

            configurationChanged = false;

            if (stepView.CloseResult == DialogCloseResult.OK)
            {
                windowResult = true;
                configurationChanged = _eParcelAustraliaViewModel.IsDirty();
            }

            return windowResult;


            // -----------------------------Start of Old Code ------------------
            //this.utils = utils;
            //if (dataObject != null && dataObject is MyData)
            //    _adaptorData = (dataObject as MyData);
            //else
            //    dataObject = _adaptorData; // Set the referance to a new instance of the data class

            //var window = new Shopify();
            //window.myData = _adaptorData;  // stored into MyData class object
            //window.utils = this.utils;
            //window.DataIntialize();      // to initialize the control on UI when click configure   
            //schemaList=window.readINI(utils.GetSystemProperty("ConnectorPath") + "\\schema\\schema.ini");
            //var windowResult = window.ShowDialog();

            //if (window.DialogResult == true)
            //{
            //    configurationChanged = true;
            //}
            //else
            //{
            //    configurationChanged = false;
            //}
            //return configurationChanged; //configuration ended with OK (false for Cancel)

            // -------------------- End of Old Code --------------------------------------
        }

        public SchemaInfo GetSchema() // returns the schema object created during configurations
        {
            SchemaInfo schemaInfo = new SchemaInfo();
            string methodName = _eParcelAustraliaViewModel.StepModel.Operation.GetAlpha();
            if (!string.IsNullOrEmpty(methodName))
                schemaInfo = GetJSonSchemaConfiguration();
            else
                System.Windows.MessageBox.Show("Schema Name is Empty. Please select appropriate schema and try again", "Mandatory File", MessageBoxButton.OK, MessageBoxImage.Error);
            return schemaInfo;
        }

        public ICheckerResult Check(ref object data, IReadOnlyResourceConfiguration resourceData)
        {
            return null; // can be used to return additional results o the builds in checker mechanism	
        }

        public bool ValidateResource(IReadOnlyResourceConfiguration resouceData, out string errorMsg)
        {
            errorMsg = null;
            if (string.IsNullOrEmpty(resouceData.GetPropertyValue("User Name").Trim()))
            {
                errorMsg += "Please enter eParcel account User Name \n";
            }
            if (string.IsNullOrEmpty(resouceData.GetPropertyValue("Password").Trim()))
            {
                errorMsg += "Please enter eParcel account Password \n";
            }
            if (string.IsNullOrEmpty(resouceData.GetPropertyValue("URL").Trim()))
            {
                errorMsg += "Please Enter base url \n";
            }
            if (string.IsNullOrEmpty(resouceData.GetPropertyValue("Account Number").Trim()))
            {
                errorMsg += "Please enter eParcel account number \n";
            }
            //This method receives read-only copy of the Resource.  
            //String myProperty = serviceData.GetPropertyValue("ServicePropertyName");
            //You should open your own dialog here with the success/failure message
            //MessageBox.Show("Success/Failure message");
            if (!string.IsNullOrEmpty(errorMsg))
            {
                System.Windows.MessageBox.Show(errorMsg);
                return false;
            }
            else
            {
                System.Windows.MessageBox.Show("Connection to the requested eParcel was established successfully.", "Information");
                return true;
            }
            //Method should return true for success and false for failure
            // return true;
        }

        public void InvokeResourceHelper(string helperID, IResourceConfiguration resouceData)
        {
            //This method receives an updatable Resource object and the name of the helper button that was pressed.  
            //In order to get a property from the Resource:
            // String myProperty = resouceData.GetPropertyValue("ResourcePropertyName");

            //You should open your own configuration dialogs



            //In order to set a properties of the Resource:

            using (IResourceConfigurationWriter writer = resouceData.BeginEditing())
            {
                writer.SetPropertyValue("TestSet", "3213jk213j1k2hj31h2k3");
                //writer.SetPropertyValue("property2","Val2");
                writer.AcceptChanges();
            }
            System.Windows.MessageBox.Show("Define your configuration dialogs and update the Resource if required");

        }

        #endregion

        public SchemaInfo GetXMLSchemaConfiguration(string methodName)
        {
            // Create the schema info based on the user entered data...
            XMLSchemaInfo xmlSchemaInfoLocal = new XMLSchemaInfo();
            xmlSchemaInfoLocal.SchemaName = "XML_Schema_Name";
            xmlSchemaInfoLocal.AlwayCreateNodes = true;
            xmlSchemaInfoLocal.AppendData = false;
            xmlSchemaInfoLocal.DataDestinationType = 0;
            xmlSchemaInfoLocal.Description = "Description...";
            xmlSchemaInfoLocal.RecursionDepth = 3;
            xmlSchemaInfoLocal.XMLEncoding = XMLSchemaInfo.UNICODE;
            xmlSchemaInfoLocal.XMLValidation = false;
            xmlSchemaInfoLocal.XSDSchemaFilePath = Path.Combine("%AddOnConnector%ISO8583", "schema", _eParcelAustraliaViewModel.StepModel.Operation.GetAlpha()) + ".xsd";
            return xmlSchemaInfoLocal;
        }

        public SchemaInfo GetJSonSchemaConfiguration()
        {
            // Create the schema info based on the user entered data...
            JSonSchemaInfo jSonSchemaInfoLocal = new JSonSchemaInfo();
            jSonSchemaInfoLocal.SchemaName = "JSON_Schema_Name";
            jSonSchemaInfoLocal.AlwayCreateNodes = true;
            jSonSchemaInfoLocal.Description = "MyTestJSon";
            jSonSchemaInfoLocal.JSonEncoding = XMLSchemaInfo.ANSI;
            jSonSchemaInfoLocal.JSonSchemaFilePath = Path.Combine("%AddOnConnector%ISO8583", "schema", _eParcelAustraliaViewModel.StepModel.EntityObjectString) + "_" + _eParcelAustraliaViewModel.StepModel.Operation.GetAlpha() + ".json";
            return jSonSchemaInfoLocal;
        }

        public SchemaInfo GetFFSchemaConfiguration()
        {
            // Create the schema info based on the user entered data...
            FlatFileSchemaInfo flatFileSchemaInfoLocal = new FlatFileSchemaInfo();
            flatFileSchemaInfoLocal.SchemaName = "FF_Schema_Name";
            flatFileSchemaInfoLocal.CreateHeaderLine = true;
            flatFileSchemaInfoLocal.DelimitedPositional = 1;
            flatFileSchemaInfoLocal.Delimiter = ",";
            flatFileSchemaInfoLocal.Lines = new List<FlatFileRecordArguments>();
            FlatFileRecordArguments line = new FlatFileRecordArguments();
            line.Name = "FirstName_A";
            line.Type = DataType.Alpha;
            line.Picture = "20";
            line.Lenght = 30;
            flatFileSchemaInfoLocal.Lines.Add(line);
            line.Name = "Last_Name_A";
            line.Type = DataType.Alpha;
            line.Picture = "20";
            line.Lenght = 30;
            flatFileSchemaInfoLocal.Lines.Add(line);
            line = new FlatFileRecordArguments();
            line.Name = "BirthDay_D";
            line.Type = DataType.Date;
            line.Picture = "DD/MM/YYYY";
            line.Lenght = 20;
            flatFileSchemaInfoLocal.Lines.Add(line);
            line = new FlatFileRecordArguments();
            line.Name = "Age_N";
            line.Type = DataType.Numeric;
            line.Picture = "3.1";
            line.Lenght = 10;
            flatFileSchemaInfoLocal.Lines.Add(line);
            line = new FlatFileRecordArguments();
            line.Name = "PhoneNo_A";
            line.Type = DataType.Alpha;
            line.Picture = "30";
            line.Lenght = 32;
            flatFileSchemaInfoLocal.Lines.Add(line);
            line = new FlatFileRecordArguments();
            line.Name = "eMail_A";
            line.Type = DataType.Alpha;
            line.Picture = "30";
            line.Lenght = 32;
            flatFileSchemaInfoLocal.Lines.Add(line);
            line = new FlatFileRecordArguments();
            line.Name = "ModifiedDay_D";
            line.Type = DataType.Date;
            line.Picture = "YYYY/MM/DD";
            line.Lenght = 20;
            flatFileSchemaInfoLocal.Lines.Add(line);
            line = new FlatFileRecordArguments();
            line.Name = "ModifiedTime_T";
            line.Type = DataType.Date;
            line.Picture = "HH:MM:SS";
            line.Lenght = 20;
            return flatFileSchemaInfoLocal;
        }
    }
}
