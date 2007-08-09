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
name|broker
operator|.
name|jmx
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|ArrayType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeDataSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
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
name|ActiveMQBytesMessage
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
name|ActiveMQStreamMessage
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
name|ActiveMQTextMessage
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
name|Message
import|;
end_import

begin_class
specifier|public
class|class
name|OpenTypeSupport
block|{
interface|interface
name|OpenTypeFactory
block|{
name|CompositeType
name|getCompositeType
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
function_decl|;
block|}
specifier|private
specifier|static
specifier|final
name|HashMap
name|OPEN_TYPE_FACTORIES
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|abstract
specifier|static
class|class
name|AbstractOpenTypeFactory
implements|implements
name|OpenTypeFactory
block|{
specifier|private
name|CompositeType
name|compositeType
decl_stmt|;
name|ArrayList
name|itemNamesList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ArrayList
name|itemDescriptionsList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|ArrayList
name|itemTypesList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|CompositeType
name|getCompositeType
parameter_list|()
throws|throws
name|OpenDataException
block|{
if|if
condition|(
name|compositeType
operator|==
literal|null
condition|)
block|{
name|init
argument_list|()
expr_stmt|;
name|compositeType
operator|=
name|createCompositeType
argument_list|()
expr_stmt|;
block|}
return|return
name|compositeType
return|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|OpenDataException
block|{         }
specifier|protected
name|CompositeType
name|createCompositeType
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|String
index|[]
name|itemNames
init|=
operator|(
name|String
index|[]
operator|)
name|itemNamesList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|itemNamesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|String
index|[]
name|itemDescriptions
init|=
operator|(
name|String
index|[]
operator|)
name|itemDescriptionsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|itemDescriptionsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|OpenType
index|[]
name|itemTypes
init|=
operator|(
name|OpenType
index|[]
operator|)
name|itemTypesList
operator|.
name|toArray
argument_list|(
operator|new
name|OpenType
index|[
name|itemTypesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeType
argument_list|(
name|getTypeName
argument_list|()
argument_list|,
name|getDescription
argument_list|()
argument_list|,
name|itemNames
argument_list|,
name|itemDescriptions
argument_list|,
name|itemTypes
argument_list|)
return|;
block|}
specifier|protected
specifier|abstract
name|String
name|getTypeName
parameter_list|()
function_decl|;
specifier|protected
name|void
name|addItem
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|,
name|OpenType
name|type
parameter_list|)
block|{
name|itemNamesList
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|itemDescriptionsList
operator|.
name|add
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|itemTypesList
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|getTypeName
argument_list|()
return|;
block|}
specifier|public
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|HashMap
name|rc
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
return|return
name|rc
return|;
block|}
block|}
specifier|static
class|class
name|MessageOpenTypeFactory
extends|extends
name|AbstractOpenTypeFactory
block|{
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|ActiveMQMessage
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSCorrelationID"
argument_list|,
literal|"JMSCorrelationID"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSDestination"
argument_list|,
literal|"JMSDestination"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSMessageID"
argument_list|,
literal|"JMSMessageID"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSReplyTo"
argument_list|,
literal|"JMSReplyTo"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSType"
argument_list|,
literal|"JMSType"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSDeliveryMode"
argument_list|,
literal|"JMSDeliveryMode"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSExpiration"
argument_list|,
literal|"JMSExpiration"
argument_list|,
name|SimpleType
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSPriority"
argument_list|,
literal|"JMSPriority"
argument_list|,
name|SimpleType
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSRedelivered"
argument_list|,
literal|"JMSRedelivered"
argument_list|,
name|SimpleType
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"JMSTimestamp"
argument_list|,
literal|"JMSTimestamp"
argument_list|,
name|SimpleType
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"Properties"
argument_list|,
literal|"Properties"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|ActiveMQMessage
name|m
init|=
operator|(
name|ActiveMQMessage
operator|)
name|o
decl_stmt|;
name|Map
name|rc
init|=
name|super
operator|.
name|getFields
argument_list|(
name|o
argument_list|)
decl_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSCorrelationID"
argument_list|,
name|m
operator|.
name|getJMSCorrelationID
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSDestination"
argument_list|,
literal|""
operator|+
name|m
operator|.
name|getJMSDestination
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSMessageID"
argument_list|,
name|m
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSReplyTo"
argument_list|,
literal|""
operator|+
name|m
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSType"
argument_list|,
name|m
operator|.
name|getJMSType
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSDeliveryMode"
argument_list|,
name|m
operator|.
name|getJMSDeliveryMode
argument_list|()
operator|==
name|DeliveryMode
operator|.
name|PERSISTENT
condition|?
literal|"PERSISTENT"
else|:
literal|"NON-PERSISTENT"
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSExpiration"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|m
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSPriority"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|m
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSRedelivered"
argument_list|,
name|Boolean
operator|.
name|valueOf
argument_list|(
name|m
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"JMSTimestamp"
argument_list|,
operator|new
name|Date
argument_list|(
name|m
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"Properties"
argument_list|,
literal|""
operator|+
name|m
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"Properties"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
specifier|static
class|class
name|ByteMessageOpenTypeFactory
extends|extends
name|MessageOpenTypeFactory
block|{
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|ActiveMQBytesMessage
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|addItem
argument_list|(
literal|"BodyLength"
argument_list|,
literal|"Body length"
argument_list|,
name|SimpleType
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|addItem
argument_list|(
literal|"BodyPreview"
argument_list|,
literal|"Body preview"
argument_list|,
operator|new
name|ArrayType
argument_list|(
literal|1
argument_list|,
name|SimpleType
operator|.
name|BYTE
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|ActiveMQBytesMessage
name|m
init|=
operator|(
name|ActiveMQBytesMessage
operator|)
name|o
decl_stmt|;
name|Map
name|rc
init|=
name|super
operator|.
name|getFields
argument_list|(
name|o
argument_list|)
decl_stmt|;
name|long
name|length
init|=
literal|0
decl_stmt|;
try|try
block|{
name|length
operator|=
name|m
operator|.
name|getBodyLength
argument_list|()
expr_stmt|;
name|rc
operator|.
name|put
argument_list|(
literal|"BodyLength"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"BodyLength"
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|byte
name|preview
index|[]
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
literal|255
argument_list|)
index|]
decl_stmt|;
name|m
operator|.
name|readBytes
argument_list|(
name|preview
argument_list|)
expr_stmt|;
comment|// This is whack! Java 1.5 JMX spec does not support primitive
comment|// arrays!
comment|// In 1.6 it seems it is supported.. but until then...
name|Byte
name|data
index|[]
init|=
operator|new
name|Byte
index|[
name|preview
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
operator|new
name|Byte
argument_list|(
name|preview
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|rc
operator|.
name|put
argument_list|(
literal|"BodyPreview"
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"BodyPreview"
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
specifier|static
class|class
name|MapMessageOpenTypeFactory
extends|extends
name|MessageOpenTypeFactory
block|{
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|ActiveMQMapMessage
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|addItem
argument_list|(
literal|"ContentMap"
argument_list|,
literal|"Content map"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|ActiveMQMapMessage
name|m
init|=
operator|(
name|ActiveMQMapMessage
operator|)
name|o
decl_stmt|;
name|Map
name|rc
init|=
name|super
operator|.
name|getFields
argument_list|(
name|o
argument_list|)
decl_stmt|;
name|long
name|length
init|=
literal|0
decl_stmt|;
try|try
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"ContentMap"
argument_list|,
literal|""
operator|+
name|m
operator|.
name|getContentMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"ContentMap"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
specifier|static
class|class
name|ObjectMessageOpenTypeFactory
extends|extends
name|MessageOpenTypeFactory
block|{
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|ActiveMQObjectMessage
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|ActiveMQObjectMessage
name|m
init|=
operator|(
name|ActiveMQObjectMessage
operator|)
name|o
decl_stmt|;
name|Map
name|rc
init|=
name|super
operator|.
name|getFields
argument_list|(
name|o
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
block|}
specifier|static
class|class
name|StreamMessageOpenTypeFactory
extends|extends
name|MessageOpenTypeFactory
block|{
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|ActiveMQStreamMessage
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|ActiveMQStreamMessage
name|m
init|=
operator|(
name|ActiveMQStreamMessage
operator|)
name|o
decl_stmt|;
name|Map
name|rc
init|=
name|super
operator|.
name|getFields
argument_list|(
name|o
argument_list|)
decl_stmt|;
return|return
name|rc
return|;
block|}
block|}
specifier|static
class|class
name|TextMessageOpenTypeFactory
extends|extends
name|MessageOpenTypeFactory
block|{
specifier|protected
name|String
name|getTypeName
parameter_list|()
block|{
return|return
name|ActiveMQTextMessage
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
throws|throws
name|OpenDataException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|addItem
argument_list|(
literal|"Text"
argument_list|,
literal|"Text"
argument_list|,
name|SimpleType
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
name|getFields
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|ActiveMQTextMessage
name|m
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|o
decl_stmt|;
name|Map
name|rc
init|=
name|super
operator|.
name|getFields
argument_list|(
name|o
argument_list|)
decl_stmt|;
try|try
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"Text"
argument_list|,
literal|""
operator|+
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|rc
operator|.
name|put
argument_list|(
literal|"Text"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
block|}
static|static
block|{
name|OPEN_TYPE_FACTORIES
operator|.
name|put
argument_list|(
name|ActiveMQMessage
operator|.
name|class
argument_list|,
operator|new
name|MessageOpenTypeFactory
argument_list|()
argument_list|)
expr_stmt|;
name|OPEN_TYPE_FACTORIES
operator|.
name|put
argument_list|(
name|ActiveMQBytesMessage
operator|.
name|class
argument_list|,
operator|new
name|ByteMessageOpenTypeFactory
argument_list|()
argument_list|)
expr_stmt|;
name|OPEN_TYPE_FACTORIES
operator|.
name|put
argument_list|(
name|ActiveMQMapMessage
operator|.
name|class
argument_list|,
operator|new
name|MapMessageOpenTypeFactory
argument_list|()
argument_list|)
expr_stmt|;
name|OPEN_TYPE_FACTORIES
operator|.
name|put
argument_list|(
name|ActiveMQObjectMessage
operator|.
name|class
argument_list|,
operator|new
name|ObjectMessageOpenTypeFactory
argument_list|()
argument_list|)
expr_stmt|;
name|OPEN_TYPE_FACTORIES
operator|.
name|put
argument_list|(
name|ActiveMQStreamMessage
operator|.
name|class
argument_list|,
operator|new
name|StreamMessageOpenTypeFactory
argument_list|()
argument_list|)
expr_stmt|;
name|OPEN_TYPE_FACTORIES
operator|.
name|put
argument_list|(
name|ActiveMQTextMessage
operator|.
name|class
argument_list|,
operator|new
name|TextMessageOpenTypeFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|OpenTypeFactory
name|getFactory
parameter_list|(
name|Class
name|clazz
parameter_list|)
throws|throws
name|OpenDataException
block|{
return|return
operator|(
name|OpenTypeFactory
operator|)
name|OPEN_TYPE_FACTORIES
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|CompositeData
name|convert
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|OpenDataException
block|{
name|OpenTypeFactory
name|f
init|=
name|getFactory
argument_list|(
name|message
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|==
literal|null
condition|)
throw|throw
operator|new
name|OpenDataException
argument_list|(
literal|"Cannot create a CompositeData for type: "
operator|+
name|message
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
name|CompositeType
name|ct
init|=
name|f
operator|.
name|getCompositeType
argument_list|()
decl_stmt|;
name|Map
name|fields
init|=
name|f
operator|.
name|getFields
argument_list|(
name|message
argument_list|)
decl_stmt|;
return|return
operator|new
name|CompositeDataSupport
argument_list|(
name|ct
argument_list|,
name|fields
argument_list|)
return|;
block|}
block|}
end_class

end_unit

