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
name|openwire
package|;
end_package

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|BeanInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|Introspector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|PropertyDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ActiveMQDestination
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
name|ActiveMQQueue
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
name|BrokerId
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
name|BrokerInfo
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
name|ConnectionId
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
name|ConsumerId
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|LocalTransactionId
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
name|MessageAck
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
name|MessageId
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
name|NetworkBridgeFilter
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
name|ProducerId
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
name|SessionId
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
name|TransactionId
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
name|filter
operator|.
name|BooleanExpression
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
name|openwire
operator|.
name|v1
operator|.
name|ActiveMQTextMessageTest
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
name|openwire
operator|.
name|v1
operator|.
name|BrokerInfoTest
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
name|openwire
operator|.
name|v1
operator|.
name|MessageAckTest
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
name|test
operator|.
name|TestSupport
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
name|util
operator|.
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DataFileGeneratorTestSupport
extends|extends
name|TestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Object
index|[]
name|EMPTY_ARGUMENTS
init|=
block|{}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DataFileGeneratorTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Throwable
name|SINGLETON_EXCEPTION
init|=
operator|new
name|Exception
argument_list|(
literal|"shared exception"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|File
name|MODULE_BASE_DIR
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|File
name|CONTROL_DIR
decl_stmt|;
static|static
block|{
name|File
name|basedir
init|=
literal|null
decl_stmt|;
try|try
block|{
name|URL
name|resource
init|=
name|DataFileGeneratorTestSupport
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"DataFileGeneratorTestSupport.class"
argument_list|)
decl_stmt|;
name|URI
name|baseURI
init|=
operator|new
name|URI
argument_list|(
name|resource
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"../../../../.."
argument_list|)
decl_stmt|;
name|basedir
operator|=
operator|new
name|File
argument_list|(
name|baseURI
argument_list|)
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|MODULE_BASE_DIR
operator|=
name|basedir
expr_stmt|;
name|CONTROL_DIR
operator|=
operator|new
name|File
argument_list|(
name|MODULE_BASE_DIR
argument_list|,
literal|"src/test/resources/openwire-control"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|counter
decl_stmt|;
specifier|private
name|OpenWireFormat
name|openWireformat
decl_stmt|;
specifier|public
name|void
name|xtestControlFileIsValid
parameter_list|()
throws|throws
name|Exception
block|{
name|generateControlFile
argument_list|()
expr_stmt|;
name|assertControlFileIsEqual
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testGenerateAndReParsingIsTheSame
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|Object
name|expected
init|=
name|createObject
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created: "
operator|+
name|expected
argument_list|)
expr_stmt|;
name|openWireformat
operator|.
name|marshal
argument_list|(
name|expected
argument_list|,
name|ds
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now lets try parse it back again
name|ByteArrayInputStream
name|in
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|DataInputStream
name|dis
init|=
operator|new
name|DataInputStream
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|Object
name|actual
init|=
name|openWireformat
operator|.
name|unmarshal
argument_list|(
name|dis
argument_list|)
decl_stmt|;
name|assertBeansEqual
argument_list|(
literal|""
argument_list|,
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Parsed: "
operator|+
name|actual
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertBeansEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|Set
argument_list|<
name|Object
argument_list|>
name|comparedObjects
parameter_list|,
name|Object
name|expected
parameter_list|,
name|Object
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
name|assertNotNull
argument_list|(
literal|"Actual object should be equal to: "
operator|+
name|expected
operator|+
literal|" but was null"
argument_list|,
name|actual
argument_list|)
expr_stmt|;
if|if
condition|(
name|comparedObjects
operator|.
name|contains
argument_list|(
name|expected
argument_list|)
condition|)
block|{
return|return;
block|}
name|comparedObjects
operator|.
name|add
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
name|type
init|=
name|expected
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Should be of same type"
argument_list|,
name|type
argument_list|,
name|actual
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|BeanInfo
name|beanInfo
init|=
name|Introspector
operator|.
name|getBeanInfo
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|PropertyDescriptor
index|[]
name|descriptors
init|=
name|beanInfo
operator|.
name|getPropertyDescriptors
argument_list|()
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
name|descriptors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PropertyDescriptor
name|descriptor
init|=
name|descriptors
index|[
name|i
index|]
decl_stmt|;
name|Method
name|method
init|=
name|descriptor
operator|.
name|getReadMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|String
name|name
init|=
name|descriptor
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Object
name|expectedValue
init|=
literal|null
decl_stmt|;
name|Object
name|actualValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|expectedValue
operator|=
name|method
operator|.
name|invoke
argument_list|(
name|expected
argument_list|,
name|EMPTY_ARGUMENTS
argument_list|)
expr_stmt|;
name|actualValue
operator|=
name|method
operator|.
name|invoke
argument_list|(
name|actual
argument_list|,
name|EMPTY_ARGUMENTS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to access property: "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
name|assertPropertyValuesEqual
argument_list|(
name|message
operator|+
name|name
argument_list|,
name|comparedObjects
argument_list|,
name|expectedValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|assertPropertyValuesEqual
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|Object
argument_list|>
name|comparedObjects
parameter_list|,
name|Object
name|expectedValue
parameter_list|,
name|Object
name|actualValue
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|message
init|=
literal|"Property "
operator|+
name|name
operator|+
literal|" not equal"
decl_stmt|;
if|if
condition|(
name|expectedValue
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
literal|"Property "
operator|+
name|name
operator|+
literal|" should be null"
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedValue
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|assertArrayEqual
argument_list|(
name|message
argument_list|,
name|comparedObjects
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|expectedValue
argument_list|,
operator|(
name|Object
index|[]
operator|)
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedValue
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|assertPrimitiveArrayEqual
argument_list|(
name|message
argument_list|,
name|comparedObjects
argument_list|,
name|expectedValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|expectedValue
operator|instanceof
name|Exception
condition|)
block|{
name|assertExceptionsEqual
argument_list|(
name|message
argument_list|,
operator|(
name|Exception
operator|)
name|expectedValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedValue
operator|instanceof
name|ByteSequence
condition|)
block|{
name|assertByteSequencesEqual
argument_list|(
name|message
argument_list|,
operator|(
name|ByteSequence
operator|)
name|expectedValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedValue
operator|instanceof
name|DataStructure
condition|)
block|{
name|assertBeansEqual
argument_list|(
name|message
operator|+
name|name
argument_list|,
name|comparedObjects
argument_list|,
name|expectedValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedValue
operator|instanceof
name|Enumeration
condition|)
block|{
name|assertEnumerationEqual
argument_list|(
name|message
operator|+
name|name
argument_list|,
name|comparedObjects
argument_list|,
operator|(
name|Enumeration
operator|)
name|expectedValue
argument_list|,
operator|(
name|Enumeration
operator|)
name|actualValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expectedValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|assertArrayEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|Set
argument_list|<
name|Object
argument_list|>
name|comparedObjects
parameter_list|,
name|Object
index|[]
name|expected
parameter_list|,
name|Object
index|[]
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Array length"
argument_list|,
name|expected
operator|.
name|length
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expected
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertPropertyValuesEqual
argument_list|(
name|message
operator|+
literal|". element: "
operator|+
name|i
argument_list|,
name|comparedObjects
argument_list|,
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertEnumerationEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|Set
argument_list|<
name|Object
argument_list|>
name|comparedObjects
parameter_list|,
name|Enumeration
name|expected
parameter_list|,
name|Enumeration
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
name|expected
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Object
name|expectedElem
init|=
name|expected
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Object
name|actualElem
init|=
name|actual
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|assertPropertyValuesEqual
argument_list|(
name|message
operator|+
literal|". element: "
operator|+
name|expectedElem
argument_list|,
name|comparedObjects
argument_list|,
name|expectedElem
argument_list|,
name|actualElem
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertPrimitiveArrayEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|Set
argument_list|<
name|Object
argument_list|>
name|comparedObjects
parameter_list|,
name|Object
name|expected
parameter_list|,
name|Object
name|actual
parameter_list|)
throws|throws
name|ArrayIndexOutOfBoundsException
throws|,
name|IllegalArgumentException
throws|,
name|Exception
block|{
name|int
name|length
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|expected
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Array length"
argument_list|,
name|length
argument_list|,
name|Array
operator|.
name|getLength
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertPropertyValuesEqual
argument_list|(
name|message
operator|+
literal|". element: "
operator|+
name|i
argument_list|,
name|comparedObjects
argument_list|,
name|Array
operator|.
name|get
argument_list|(
name|expected
argument_list|,
name|i
argument_list|)
argument_list|,
name|Array
operator|.
name|get
argument_list|(
name|actual
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertByteSequencesEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|ByteSequence
name|expected
parameter_list|,
name|Object
name|actualValue
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|message
operator|+
literal|". Actual value should be a ByteSequence but was: "
operator|+
name|actualValue
argument_list|,
name|actualValue
operator|instanceof
name|ByteSequence
argument_list|)
expr_stmt|;
name|ByteSequence
name|actual
init|=
operator|(
name|ByteSequence
operator|)
name|actualValue
decl_stmt|;
name|int
name|length
init|=
name|expected
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Length"
argument_list|,
name|length
argument_list|,
name|actual
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|offset
init|=
name|expected
operator|.
name|getOffset
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Offset"
argument_list|,
name|offset
argument_list|,
name|actual
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|expected
operator|.
name|getData
argument_list|()
decl_stmt|;
name|byte
index|[]
name|actualData
init|=
name|actual
operator|.
name|getData
argument_list|()
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Offset "
operator|+
name|i
argument_list|,
name|data
index|[
name|offset
operator|+
name|i
index|]
argument_list|,
name|actualData
index|[
name|offset
operator|+
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertExceptionsEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|expected
parameter_list|,
name|Object
name|actualValue
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|message
operator|+
literal|". Actual value should be an exception but was: "
operator|+
name|actualValue
argument_list|,
name|actualValue
operator|instanceof
name|Exception
argument_list|)
expr_stmt|;
name|Exception
name|actual
init|=
operator|(
name|Exception
operator|)
name|actualValue
decl_stmt|;
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expected
operator|.
name|getMessage
argument_list|()
argument_list|,
name|actual
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|openWireformat
operator|=
name|createOpenWireFormat
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|generateControlFile
parameter_list|()
throws|throws
name|Exception
block|{
name|CONTROL_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
name|CONTROL_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".bin"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|openWireformat
operator|.
name|marshal
argument_list|(
name|createObject
argument_list|()
argument_list|,
name|ds
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|InputStream
name|generateInputStream
parameter_list|()
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|ds
init|=
operator|new
name|DataOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|openWireformat
operator|.
name|marshal
argument_list|(
name|createObject
argument_list|()
argument_list|,
name|ds
argument_list|)
expr_stmt|;
name|ds
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|assertControlFileIsEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|dataFile
init|=
operator|new
name|File
argument_list|(
name|CONTROL_DIR
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".bin"
argument_list|)
decl_stmt|;
name|FileInputStream
name|is1
init|=
operator|new
name|FileInputStream
argument_list|(
name|dataFile
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
try|try
block|{
name|InputStream
name|is2
init|=
name|generateInputStream
argument_list|()
decl_stmt|;
name|int
name|a
init|=
name|is1
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|b
init|=
name|is2
operator|.
name|read
argument_list|()
decl_stmt|;
name|pos
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Data does not match control file: "
operator|+
name|dataFile
operator|+
literal|" at byte position "
operator|+
name|pos
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
while|while
condition|(
name|a
operator|>=
literal|0
operator|&&
name|b
operator|>=
literal|0
condition|)
block|{
name|a
operator|=
name|is1
operator|.
name|read
argument_list|()
expr_stmt|;
name|b
operator|=
name|is2
operator|.
name|read
argument_list|()
expr_stmt|;
name|pos
operator|++
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Data does not match control file: "
operator|+
name|dataFile
operator|+
literal|" at byte position "
operator|+
name|pos
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
name|is2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|is1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|Object
name|createObject
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|protected
name|void
name|populateObject
parameter_list|(
name|Object
name|info
parameter_list|)
throws|throws
name|Exception
block|{
comment|// empty method to allow derived classes to call super
comment|// to simplify generated code
block|}
specifier|protected
name|OpenWireFormat
name|createOpenWireFormat
parameter_list|()
block|{
name|OpenWireFormat
name|wf
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
name|wf
operator|.
name|setCacheEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setStackTraceEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|wf
operator|.
name|setVersion
argument_list|(
name|OpenWireFormat
operator|.
name|DEFAULT_VERSION
argument_list|)
expr_stmt|;
return|return
name|wf
return|;
block|}
specifier|protected
name|BrokerId
name|createBrokerId
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
operator|new
name|BrokerId
argument_list|(
name|text
argument_list|)
return|;
block|}
specifier|protected
name|TransactionId
name|createTransactionId
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|LocalTransactionId
argument_list|(
name|createConnectionId
argument_list|(
name|string
argument_list|)
argument_list|,
operator|++
name|counter
argument_list|)
return|;
block|}
specifier|protected
name|ConnectionId
name|createConnectionId
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|ConnectionId
argument_list|(
name|string
argument_list|)
return|;
block|}
specifier|protected
name|SessionId
name|createSessionId
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|SessionId
argument_list|(
name|createConnectionId
argument_list|(
name|string
argument_list|)
argument_list|,
operator|++
name|counter
argument_list|)
return|;
block|}
specifier|protected
name|ProducerId
name|createProducerId
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|ProducerId
argument_list|(
name|createSessionId
argument_list|(
name|string
argument_list|)
argument_list|,
operator|++
name|counter
argument_list|)
return|;
block|}
specifier|protected
name|ConsumerId
name|createConsumerId
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|ConsumerId
argument_list|(
name|createSessionId
argument_list|(
name|string
argument_list|)
argument_list|,
operator|++
name|counter
argument_list|)
return|;
block|}
specifier|protected
name|MessageId
name|createMessageId
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|MessageId
argument_list|(
name|createProducerId
argument_list|(
name|string
argument_list|)
argument_list|,
operator|++
name|counter
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQDestination
name|createActiveMQDestination
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|string
argument_list|)
return|;
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQTextMessage
name|message
init|=
operator|(
name|ActiveMQTextMessage
operator|)
name|ActiveMQTextMessageTest
operator|.
name|SINGLETON
operator|.
name|createObject
argument_list|()
decl_stmt|;
name|message
operator|.
name|setText
argument_list|(
name|string
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|protected
name|BrokerInfo
name|createBrokerInfo
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
name|BrokerInfo
operator|)
name|BrokerInfoTest
operator|.
name|SINGLETON
operator|.
name|createObject
argument_list|()
return|;
block|}
specifier|protected
name|MessageAck
name|createMessageAck
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
name|MessageAck
operator|)
name|MessageAckTest
operator|.
name|SINGLETON
operator|.
name|createObject
argument_list|()
return|;
block|}
specifier|protected
name|DataStructure
name|createDataStructure
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|createBrokerInfo
argument_list|(
name|string
argument_list|)
return|;
block|}
specifier|protected
name|Throwable
name|createThrowable
parameter_list|(
name|String
name|string
parameter_list|)
block|{
comment|// we have issues with stack frames not being equal so share the same
comment|// exception each time
return|return
name|SINGLETON_EXCEPTION
return|;
block|}
specifier|protected
name|BooleanExpression
name|createBooleanExpression
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
operator|new
name|NetworkBridgeFilter
argument_list|(
operator|new
name|BrokerId
argument_list|(
name|string
argument_list|)
argument_list|,
literal|10
argument_list|)
return|;
block|}
block|}
end_class

end_unit

