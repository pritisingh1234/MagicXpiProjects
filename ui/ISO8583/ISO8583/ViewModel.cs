using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ISO8583
{
    public class ViewModel
    {
        public string Key { get; set; }

        public string Value { get; set; }
        public ViewModel(string id, string methodname)
        {
            Key = id;
            Value = methodname;
        }

    }
}
