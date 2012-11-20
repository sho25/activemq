begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|stomp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|advisory
operator|.
name|AdvisorySupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerContextAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMapMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQObjectMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|DataStructure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|mapped
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|fusesource
operator|.
name|hawtbuf
operator|.
name|UTF8Buffer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|XStream
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|converters
operator|.
name|basic
operator|.
name|AbstractSingleValueConverter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|HierarchicalStreamReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|HierarchicalStreamWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|json
operator|.
name|JettisonMappedXmlDriver
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|xml
operator|.
name|PrettyPrintWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|xml
operator|.
name|XppReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thoughtworks
operator|.
name|xstream
operator|.
name|io
operator|.
name|xml
operator|.
name|xppdom
operator|.
name|XppFactory
import|;
end_import

begin_comment
comment|/**  * Frame translator implementation that uses XStream to convert messages to and  * from XML and JSON  *  * @author<a href="mailto:dejan@nighttale.net">Dejan Bosanac</a>  */
end_comment

begin_class
specifier|public
class|class
name|JmsFrameTranslator
extends|extends
name|LegacyFrameTranslator
implements|implements
name|BrokerContextAware
block|{
name|XStream
name|xStream
init|=
literal|null
decl_stmt|;
name|BrokerContext
name|brokerContext
decl_stmt|;
annotation|@
name|Override
specifier|public
name|ActiveMQMessage
name|convertFrame
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|StompFrame
name|command
parameter_list|)
throws|throws
name|JMSException
throws|,
name|ProtocolException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
name|command
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
name|ActiveMQMessage
name|msg
decl_stmt|;
name|String
name|transformation
init|=
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|headers
operator|.
name|containsKey
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|CONTENT_LENGTH
argument_list|)
operator|||
name|transformation
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_BYTE
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|msg
operator|=
name|super
operator|.
name|convertFrame
argument_list|(
name|converter
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|HierarchicalStreamReader
name|in
decl_stmt|;
try|try
block|{
name|String
name|text
init|=
operator|new
name|String
argument_list|(
name|command
operator|.
name|getContent
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|Stomp
operator|.
name|Transformations
operator|.
name|getValue
argument_list|(
name|transformation
argument_list|)
condition|)
block|{
case|case
name|JMS_OBJECT_XML
case|:
name|in
operator|=
operator|new
name|XppReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|,
name|XppFactory
operator|.
name|createDefaultParser
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|=
name|createObjectMessage
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|JMS_OBJECT_JSON
case|:
name|in
operator|=
operator|new
name|JettisonMappedXmlDriver
argument_list|()
operator|.
name|createReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|=
name|createObjectMessage
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|JMS_MAP_XML
case|:
name|in
operator|=
operator|new
name|XppReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|,
name|XppFactory
operator|.
name|createDefaultParser
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|=
name|createMapMessage
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
case|case
name|JMS_MAP_JSON
case|:
name|in
operator|=
operator|new
name|JettisonMappedXmlDriver
argument_list|()
operator|.
name|createReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|=
name|createMapMessage
argument_list|(
name|in
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Unkown transformation: "
operator|+
name|transformation
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|command
operator|.
name|getHeaders
argument_list|()
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|=
name|super
operator|.
name|convertFrame
argument_list|(
name|converter
argument_list|,
name|command
argument_list|)
expr_stmt|;
block|}
block|}
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromFrameToMessage
argument_list|(
name|converter
argument_list|,
name|command
argument_list|,
name|msg
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|msg
return|;
block|}
annotation|@
name|Override
specifier|public
name|StompFrame
name|convertMessage
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|ActiveMQMessage
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQObjectMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|StompFrame
name|command
init|=
operator|new
name|StompFrame
argument_list|()
decl_stmt|;
name|command
operator|.
name|setAction
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|command
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromMessageToFrame
argument_list|(
name|converter
argument_list|,
name|message
argument_list|,
name|command
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_XML
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|,
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_OBJECT_XML
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_JSON
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|,
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_OBJECT_JSON
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ActiveMQObjectMessage
name|msg
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|marshall
argument_list|(
name|msg
operator|.
name|getObject
argument_list|()
argument_list|,
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
elseif|else
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQMapMessage
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|StompFrame
name|command
init|=
operator|new
name|StompFrame
argument_list|()
decl_stmt|;
name|command
operator|.
name|setAction
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|command
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromMessageToFrame
argument_list|(
name|converter
argument_list|,
name|message
argument_list|,
name|command
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_XML
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|,
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_MAP_XML
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_JSON
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|,
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_MAP_JSON
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ActiveMQMapMessage
name|msg
init|=
operator|(
name|ActiveMQMapMessage
operator|)
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|marshall
argument_list|(
operator|(
name|Serializable
operator|)
name|msg
operator|.
name|getContentMap
argument_list|()
argument_list|,
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
elseif|else
if|if
condition|(
name|message
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ActiveMQMessage
operator|.
name|DATA_STRUCTURE_TYPE
operator|&&
name|AdvisorySupport
operator|.
name|ADIVSORY_MESSAGE_TYPE
operator|.
name|equals
argument_list|(
name|message
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|StompFrame
name|command
init|=
operator|new
name|StompFrame
argument_list|()
decl_stmt|;
name|command
operator|.
name|setAction
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|command
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|FrameTranslator
operator|.
name|Helper
operator|.
name|copyStandardHeadersFromMessageToFrame
argument_list|(
name|converter
argument_list|,
name|message
argument_list|,
name|command
argument_list|,
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_XML
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|,
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_ADVISORY_XML
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
operator|.
name|equals
argument_list|(
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_JSON
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|,
name|Stomp
operator|.
name|Transformations
operator|.
name|JMS_ADVISORY_JSON
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|body
init|=
name|marshallAdvisory
argument_list|(
name|message
operator|.
name|getDataStructure
argument_list|()
argument_list|,
name|headers
operator|.
name|get
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|TRANSFORMATION
argument_list|)
argument_list|)
decl_stmt|;
name|command
operator|.
name|setContent
argument_list|(
name|body
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|convertMessage
argument_list|(
name|converter
argument_list|,
name|message
argument_list|)
return|;
block|}
block|}
comment|/**      * Marshalls the Object to a string using XML or JSON encoding      */
specifier|protected
name|String
name|marshall
parameter_list|(
name|Serializable
name|object
parameter_list|,
name|String
name|transformation
parameter_list|)
throws|throws
name|JMSException
block|{
name|StringWriter
name|buffer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|HierarchicalStreamWriter
name|out
decl_stmt|;
if|if
condition|(
name|transformation
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|"json"
argument_list|)
condition|)
block|{
name|out
operator|=
operator|new
name|JettisonMappedXmlDriver
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|createWriter
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|=
operator|new
name|PrettyPrintWriter
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|getXStream
argument_list|()
operator|.
name|marshal
argument_list|(
name|object
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|ActiveMQObjectMessage
name|createObjectMessage
parameter_list|(
name|HierarchicalStreamReader
name|in
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQObjectMessage
name|objMsg
init|=
operator|new
name|ActiveMQObjectMessage
argument_list|()
decl_stmt|;
name|Object
name|obj
init|=
name|getXStream
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|objMsg
operator|.
name|setObject
argument_list|(
operator|(
name|Serializable
operator|)
name|obj
argument_list|)
expr_stmt|;
return|return
name|objMsg
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|ActiveMQMapMessage
name|createMapMessage
parameter_list|(
name|HierarchicalStreamReader
name|in
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQMapMessage
name|mapMsg
init|=
operator|new
name|ActiveMQMapMessage
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|getXStream
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|in
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
name|mapMsg
operator|.
name|setObject
argument_list|(
name|key
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|mapMsg
return|;
block|}
specifier|protected
name|String
name|marshallAdvisory
parameter_list|(
specifier|final
name|DataStructure
name|ds
parameter_list|,
name|String
name|transformation
parameter_list|)
block|{
name|StringWriter
name|buffer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|HierarchicalStreamWriter
name|out
decl_stmt|;
if|if
condition|(
name|transformation
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|endsWith
argument_list|(
literal|"json"
argument_list|)
condition|)
block|{
name|out
operator|=
operator|new
name|JettisonMappedXmlDriver
argument_list|()
operator|.
name|createWriter
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|=
operator|new
name|PrettyPrintWriter
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|XStream
name|xstream
init|=
name|getXStream
argument_list|()
decl_stmt|;
name|xstream
operator|.
name|setMode
argument_list|(
name|XStream
operator|.
name|NO_REFERENCES
argument_list|)
expr_stmt|;
name|xstream
operator|.
name|aliasPackage
argument_list|(
literal|""
argument_list|,
literal|"org.apache.activemq.command"
argument_list|)
expr_stmt|;
name|xstream
operator|.
name|marshal
argument_list|(
name|ds
argument_list|,
name|out
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|XStream
name|getXStream
parameter_list|()
block|{
if|if
condition|(
name|xStream
operator|==
literal|null
condition|)
block|{
name|xStream
operator|=
name|createXStream
argument_list|()
expr_stmt|;
block|}
return|return
name|xStream
return|;
block|}
specifier|public
name|void
name|setXStream
parameter_list|(
name|XStream
name|xStream
parameter_list|)
block|{
name|this
operator|.
name|xStream
operator|=
name|xStream
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
name|XStream
name|createXStream
parameter_list|()
block|{
name|XStream
name|xstream
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|brokerContext
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|XStream
argument_list|>
name|beans
init|=
name|brokerContext
operator|.
name|getBeansOfType
argument_list|(
name|XStream
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|XStream
name|bean
range|:
name|beans
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|bean
operator|!=
literal|null
condition|)
block|{
name|xstream
operator|=
name|bean
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|xstream
operator|==
literal|null
condition|)
block|{
name|xstream
operator|=
operator|new
name|XStream
argument_list|()
expr_stmt|;
block|}
comment|// For any object whose elements contains an UTF8Buffer instance instead of a String
comment|// type we map it to String both in and out such that we don't marshal UTF8Buffers out
name|xstream
operator|.
name|registerConverter
argument_list|(
operator|new
name|AbstractSingleValueConverter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|fromString
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
specifier|public
name|boolean
name|canConvert
parameter_list|(
name|Class
name|type
parameter_list|)
block|{
return|return
name|type
operator|.
name|equals
argument_list|(
name|UTF8Buffer
operator|.
name|class
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|xstream
operator|.
name|alias
argument_list|(
literal|"string"
argument_list|,
name|UTF8Buffer
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|xstream
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBrokerContext
parameter_list|(
name|BrokerContext
name|brokerContext
parameter_list|)
block|{
name|this
operator|.
name|brokerContext
operator|=
name|brokerContext
expr_stmt|;
block|}
block|}
end_class

end_unit

