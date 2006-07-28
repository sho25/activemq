begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
package|;
end_package

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
name|jms
operator|.
name|MessageFormatException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageNotReadableException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageNotWriteableException
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
name|CommandTypes
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQBytesMessageTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|ActiveMQBytesMessageTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/*      * @see TestCase#setUp()      */
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
block|}
comment|/*      * @see TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructor for ActiveMQBytesMessageTest.      *      * @param arg0      */
specifier|public
name|ActiveMQBytesMessageTest
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetDataStructureType
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|msg
operator|.
name|getDataStructureType
argument_list|()
argument_list|,
name|CommandTypes
operator|.
name|ACTIVEMQ_BYTES_MESSAGE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetBodyLength
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|int
name|len
init|=
literal|10
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|msg
operator|.
name|writeLong
argument_list|(
literal|5l
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|getBodyLength
argument_list|()
operator|==
operator|(
name|len
operator|*
literal|8
operator|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadBoolean
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadByte
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readByte
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadUnsignedByte
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readUnsignedByte
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadShort
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|3000
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readShort
argument_list|()
operator|==
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadUnsignedShort
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|3000
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readUnsignedShort
argument_list|()
operator|==
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadChar
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeChar
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readChar
argument_list|()
operator|==
literal|'a'
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadInt
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeInt
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readInt
argument_list|()
operator|==
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadLong
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeLong
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readLong
argument_list|()
operator|==
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadFloat
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeFloat
argument_list|(
literal|3.3f
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readFloat
argument_list|()
operator|==
literal|3.3f
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadDouble
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeDouble
argument_list|(
literal|3.3d
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readDouble
argument_list|()
operator|==
literal|3.3d
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReadUTF
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|str
init|=
literal|"this is a test"
decl_stmt|;
name|msg
operator|.
name|writeUTF
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|readUTF
argument_list|()
operator|.
name|equals
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*      * Class to test for int readBytes(byte[])      */
specifier|public
name|void
name|testReadBytesbyteArray
parameter_list|()
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|50
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
operator|(
name|byte
operator|)
name|i
expr_stmt|;
block|}
name|msg
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|msg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|byte
index|[]
name|test
init|=
operator|new
name|byte
index|[
name|data
operator|.
name|length
index|]
decl_stmt|;
name|msg
operator|.
name|readBytes
argument_list|(
name|test
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
name|test
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|test
index|[
name|i
index|]
operator|==
name|i
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|jmsEx
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testWriteObject
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|writeObject
argument_list|(
literal|"fred"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Character
argument_list|(
literal|'q'
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Byte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Short
argument_list|(
operator|(
name|short
operator|)
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Integer
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Long
argument_list|(
literal|300l
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Float
argument_list|(
literal|3.3f
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Double
argument_list|(
literal|3.3
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|byte
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageFormatException
name|mfe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"objectified primitives should be allowed"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|msg
operator|.
name|writeObject
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"only objectified primitives are allowed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageFormatException
name|mfe
parameter_list|)
block|{         }
block|}
comment|/* new */
specifier|public
name|void
name|testClearBody
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQBytesMessage
name|bytesMessage
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|bytesMessage
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|bytesMessage
operator|.
name|clearBody
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|bytesMessage
operator|.
name|isReadOnlyBody
argument_list|()
argument_list|)
expr_stmt|;
name|bytesMessage
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|bytesMessage
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|mnwe
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQBytesMessage
name|message
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|message
operator|.
name|writeDouble
argument_list|(
literal|24.5
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeLong
argument_list|(
literal|311
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"should be writeable"
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|assertTrue
argument_list|(
name|message
operator|.
name|isReadOnlyBody
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|.
name|readDouble
argument_list|()
argument_list|,
literal|24.5
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|.
name|readLong
argument_list|()
argument_list|,
literal|311
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|mnre
parameter_list|)
block|{
name|fail
argument_list|(
literal|"should be readable"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|message
operator|.
name|writeInt
argument_list|(
literal|33
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should throw exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testReadOnlyBody
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQBytesMessage
name|message
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|message
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|3
index|]
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeChar
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeDouble
argument_list|(
literal|1.5
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeFloat
argument_list|(
operator|(
name|float
operator|)
literal|1.5
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeLong
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeObject
argument_list|(
literal|"stringobj"
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeUTF
argument_list|(
literal|"utfstring"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should be writeable"
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|message
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|message
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|message
operator|.
name|readUnsignedByte
argument_list|()
expr_stmt|;
name|message
operator|.
name|readBytes
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|message
operator|.
name|readBytes
argument_list|(
operator|new
name|byte
index|[
literal|2
index|]
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|readChar
argument_list|()
expr_stmt|;
name|message
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|message
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|message
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|message
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|message
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|message
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|message
operator|.
name|readUnsignedShort
argument_list|()
expr_stmt|;
name|message
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|mnwe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should be readable"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|message
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|3
index|]
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeChar
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeDouble
argument_list|(
literal|1.5
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeFloat
argument_list|(
operator|(
name|float
operator|)
literal|1.5
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeLong
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeObject
argument_list|(
literal|"stringobj"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|writeUTF
argument_list|(
literal|"utfstring"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|testWriteOnlyBody
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQBytesMessage
name|message
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|clearBody
argument_list|()
expr_stmt|;
try|try
block|{
name|message
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|3
index|]
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeChar
argument_list|(
literal|'a'
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeDouble
argument_list|(
literal|1.5
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeFloat
argument_list|(
operator|(
name|float
operator|)
literal|1.5
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeInt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeLong
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeObject
argument_list|(
literal|"stringobj"
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|writeUTF
argument_list|(
literal|"utfstring"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotWriteableException
name|mnwe
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Should be writeable"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|message
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|mnwe
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readUnsignedByte
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readBytes
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readBytes
argument_list|(
operator|new
name|byte
index|[
literal|2
index|]
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readChar
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readUnsignedShort
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
try|try
block|{
name|message
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{         }
block|}
block|}
end_class

end_unit

