using System;
using System.Collections.Generic;
using System.Xml.Serialization;

namespace ISO8583
{

    [XmlRoot("MethodList")]
    public class MethodList
    {
        [XmlElement("Method")]
        public List<MethodEntity> MethodEntityList { get; set; }

        public MethodList()
        {

        }

        public MethodList(List<MethodEntity> methodEntityList)
        {
            MethodEntityList = new List<MethodEntity>();
            MethodEntityList = methodEntityList;
        }
    }

    [Serializable()]
    public class MethodEntity
    {
        [XmlElement("Name")]
        public string Name { get; set; }

        [XmlElement("OperationList")]
        public string OperationList { get; set; }
    }
}
