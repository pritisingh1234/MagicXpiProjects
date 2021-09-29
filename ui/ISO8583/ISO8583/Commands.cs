using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace ISO8583
{
    static class Commands
    {
        public static readonly RoutedCommand PromptCommand = new RoutedCommand("Prompt", typeof(Commands));
        public static readonly RoutedCommand EditExpressionCommand = new RoutedCommand("EditExpression", typeof(Commands));
    }
}
