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
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|beans
operator|.
name|Transient
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
name|IOException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
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
name|ByteArrayOutputStream
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
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|MarshallingSupport
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQTextMessageTest
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
name|TestRunner
operator|.
name|run
argument_list|(
name|ActiveMQTextMessageTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGetDataStructureType
parameter_list|()
block|{
name|ActiveMQTextMessage
name|msg
init|=
operator|new
name|ActiveMQTextMessage
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
name|ACTIVEMQ_TEXT_MESSAGE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testShallowCopy
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQTextMessage
name|msg
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|String
name|string
init|=
literal|"str"
decl_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|Message
name|copy
init|=
name|msg
operator|.
name|copy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
operator|==
operator|(
operator|(
name|ActiveMQTextMessage
operator|)
name|copy
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSetText
parameter_list|()
block|{
name|ActiveMQTextMessage
name|msg
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|String
name|str
init|=
literal|"testText"
decl_stmt|;
try|try
block|{
name|msg
operator|.
name|setText
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|str
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testGetBytes
parameter_list|()
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|ActiveMQTextMessage
name|msg
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|String
name|str
init|=
literal|"testText"
decl_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|msg
operator|.
name|beforeMarshall
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|ByteSequence
name|bytes
init|=
name|msg
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|msg
operator|=
operator|new
name|ActiveMQTextMessage
argument_list|()
expr_stmt|;
name|msg
operator|.
name|setContent
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
operator|.
name|getText
argument_list|()
argument_list|,
name|str
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testClearBody
parameter_list|()
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|ActiveMQTextMessage
name|textMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|textMessage
operator|.
name|setText
argument_list|(
literal|"string"
argument_list|)
expr_stmt|;
name|textMessage
operator|.
name|clearBody
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|textMessage
operator|.
name|isReadOnlyBody
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|textMessage
operator|.
name|setText
argument_list|(
literal|"String"
argument_list|)
expr_stmt|;
name|textMessage
operator|.
name|getText
argument_list|()
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
block|}
specifier|public
name|void
name|testReadOnlyBody
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQTextMessage
name|textMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|textMessage
operator|.
name|setText
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|textMessage
operator|.
name|setReadOnlyBody
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|textMessage
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
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
name|textMessage
operator|.
name|setText
argument_list|(
literal|"test"
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
name|testWriteOnlyBody
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// should always be readable
name|ActiveMQTextMessage
name|textMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|textMessage
operator|.
name|setReadOnlyBody
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|textMessage
operator|.
name|setText
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|textMessage
operator|.
name|getText
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessageNotReadableException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"should be readable"
argument_list|)
expr_stmt|;
block|}
name|textMessage
operator|.
name|setReadOnlyBody
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|textMessage
operator|.
name|getText
argument_list|()
expr_stmt|;
name|textMessage
operator|.
name|setText
argument_list|(
literal|"test"
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
name|MessageNotReadableException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"should be readable"
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
name|testShortText
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|shortText
init|=
literal|"Content"
decl_stmt|;
name|ActiveMQTextMessage
name|shortMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|setContent
argument_list|(
name|shortMessage
argument_list|,
name|shortText
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|shortMessage
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"text = "
operator|+
name|shortText
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|shortMessage
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
name|shortText
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|longText
init|=
literal|"Very very very very veeeeeeery loooooooooooooooooooooooooooooooooong text"
decl_stmt|;
name|String
name|longExpectedText
init|=
literal|"Very very very very veeeeeeery looooooooooooo...ooooong text"
decl_stmt|;
name|ActiveMQTextMessage
name|longMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|setContent
argument_list|(
name|longMessage
argument_list|,
name|longText
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|longMessage
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"text = "
operator|+
name|longExpectedText
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|longMessage
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
name|longText
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNullText
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTextMessage
name|nullMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|setContent
argument_list|(
name|nullMessage
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nullMessage
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"text = null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTransient
parameter_list|()
throws|throws
name|Exception
block|{
name|Method
name|method
init|=
name|ActiveMQTextMessage
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"getRegionDestination"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|method
operator|.
name|isAnnotationPresent
argument_list|(
name|Transient
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setContent
parameter_list|(
name|Message
name|message
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|dataOut
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|MarshallingSupport
operator|.
name|writeUTF8
argument_list|(
name|dataOut
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|dataOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|message
operator|.
name|setContent
argument_list|(
name|baos
operator|.
name|toByteSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

