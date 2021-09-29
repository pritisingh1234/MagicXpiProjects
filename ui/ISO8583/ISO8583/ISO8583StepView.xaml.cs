using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using Magicsoftware.iBolt.Common;
using Magicsoftware.iBolt.Common.Help;
using Magicsoftware.iBolt.Common.StudioError;
using MagicSoftware.iBolt.Common;
using MagicSoftware.iBolt.Common.Controls;

namespace ISO8583
{
    /// <summary>
    /// Interaction logic for ShopifyStepView.xaml
    /// </summary>
    public partial class ISO8583StepView : UserControl, IiBGeneric, IVsCommandHandler
    {
        private ISO8583ViewModel _viewmodel;
        public DialogCloseResult CloseResult { get; private set; }
        public static readonly RoutedCommand OkButtonCommand = new RoutedCommand("OKButtonCommand", typeof(ISO8583StepView));
        public static readonly RoutedCommand ObjectEntityButtonCommand = new RoutedCommand("ObjectEntityButtonCommand", typeof(ISO8583StepView));

        private bool _isConfigExist = false;

        public ISO8583StepView()
        {
            InitializeComponent();
        }

        public ISO8583StepView(ISO8583ViewModel viewmodel, bool isConfigExist)
        {
            InitializeComponent();
            _viewmodel = viewmodel;
            DataContext = _viewmodel;
            CommandBindings.Add(new CommandBinding(OkButtonCommand, BtnOk_OnClick));
            BtnOk.Command = OkButtonCommand;

            CommandBindings.Add(new CommandBinding(ObjectEntityButtonCommand, BtnObjectEntity_OnClick));
            BtnObjectEntity.Command = ObjectEntityButtonCommand;
        }


        private void EditExpression_OnExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            _viewmodel.EditExpression((string)e.Parameter);
        }

        private void PromptCommand_OnExecuted(object sender, ExecutedRoutedEventArgs e)
        {
            _viewmodel.Prompt((string)e.Parameter);
        }

        public string GetTitle()
        {
            // throw new NotImplementedException();
            return "eParcelAustralia Configuration";
        }

        public bool IsSaveValid(out string ctrlName, out string errorMessage)
        {
            //throw new NotImplementedException();
            ctrlName = string.Empty;
            errorMessage = string.Empty;

            if (_viewmodel.ResultStorage != null && _viewmodel.ResultStorage.Value == null)
            {
                return CheckResultStorage(out errorMessage);
            }

            if (_viewmodel.ResultStorage != null && _viewmodel.ResultStorage.Value != null && string.IsNullOrEmpty(_viewmodel.ResultStorage.Value.ToString()))
            {
                return CheckResultStorage(out errorMessage);
            }

            if (_viewmodel.OperationSuccess != null && _viewmodel.OperationSuccess.Value != null && string.IsNullOrEmpty(_viewmodel.OperationSuccess.Value.ToString()))
            {
                errorMessage = "Select a variable to store operation's status.";
                return false;
            }

            return true;
        }

        private bool CheckResultStorage(out string errorMessage)
        {
            errorMessage = string.Empty;
            if (_viewmodel.ResultStorageOption.Item1.Equals("Variable"))
            {
                errorMessage = "Select a variable for Store Result In.";
                return false;
            }

            if (_viewmodel.ResultStorageOption.Item1.Equals("File"))
            {
                errorMessage = "Select a file for Store Result In.";
                return false;
            }

            if (_viewmodel.ResultStorageOption.Item1.Equals("SplitFile"))
            {
                _viewmodel.StepModel.StoreSplitFile.SetValue("SplitFile");
                // errorMessage = "Select a file for Store Result In.";
                return true;
            }
            return true;
        }

        public bool IsDirty()
        {
            return _viewmodel.IsDirty();
        }

        public void SaveData()
        {
            _viewmodel.CommitChanges();
            CloseResult = DialogCloseResult.OK;
        }

        public void NavigateToError()
        {
            //throw new NotImplementedException();
        }

        public string GetHelpKey()
        {
            //throw new NotImplementedException();
            return string.Empty;
        }

        private void BtnObjectEntity_OnClick(object sender, RoutedEventArgs e)
        {
            _viewmodel.ShowEntityObject();
        }

        private void BtnOk_OnClick(object sender, RoutedEventArgs e)
        {
            if (onClose != null)
                onClose();
        }

        private void BtnCancel_OnClick(object sender, RoutedEventArgs e)
        {
            CloseResult = DialogCloseResult.Cancel;
            if (onCancel != null)
                onCancel();
        }

        public NavigateArgs ErrorToNavigate { get; set; }
        public List<NavigateArgs> NavigateArgsErrorsCollection { get; set; }
        public event SaveData onSave;
        public event CancelData onCancel;
        public event CloseWindow onClose;
        public void HandleCommand(CommandType command)
        {
            switch (command.ID)
            {
                case StudioCommand.Help:
                    {
                        HelpPage.ShowHelp(GetHelpKey());
                        break;
                    }
            }
        }

        public bool CanExec(CommandType command)
        {
            return true;
        }
    }
}
