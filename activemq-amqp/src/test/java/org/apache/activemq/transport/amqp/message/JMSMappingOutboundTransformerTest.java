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
name|amqp
operator|.
name|message
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|Symbol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|AmqpValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|messaging
operator|.
name|MessageAnnotations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|message
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
specifier|public
class|class
name|JMSMappingOutboundTransformerTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithTextMessageCreatesAmqpValueStringBody
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|contentString
init|=
literal|"myTextMessageContent"
decl_stmt|;
name|TextMessage
name|mockTextMessage
init|=
name|createMockTextMessage
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockTextMessage
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|contentString
argument_list|)
expr_stmt|;
name|JMSVendor
name|mockVendor
init|=
name|createMockVendor
argument_list|()
decl_stmt|;
name|JMSMappingOutboundTransformer
name|transformer
init|=
operator|new
name|JMSMappingOutboundTransformer
argument_list|(
name|mockVendor
argument_list|)
decl_stmt|;
name|Message
name|amqp
init|=
name|transformer
operator|.
name|convert
argument_list|(
name|mockTextMessage
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|amqp
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|amqp
operator|.
name|getBody
argument_list|()
operator|instanceof
name|AmqpValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|contentString
argument_list|,
operator|(
operator|(
name|AmqpValue
operator|)
name|amqp
operator|.
name|getBody
argument_list|()
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// ======= JMSDestination Handling =========
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSDestinationNull
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestConvertMessageWithJMSDestination
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSDestinationQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|Queue
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Queue
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSDestination
argument_list|(
name|mockDest
argument_list|,
literal|"queue"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSDestinationTemporaryQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|TemporaryQueue
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TemporaryQueue
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSDestination
argument_list|(
name|mockDest
argument_list|,
literal|"temporary,queue"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSDestinationTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|Topic
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Topic
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSDestination
argument_list|(
name|mockDest
argument_list|,
literal|"topic"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSDestinationTemporaryTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|TemporaryTopic
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TemporaryTopic
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSDestination
argument_list|(
name|mockDest
argument_list|,
literal|"temporary,topic"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestConvertMessageWithJMSDestination
parameter_list|(
name|Destination
name|jmsDestination
parameter_list|,
name|Object
name|expectedAnnotationValue
parameter_list|)
throws|throws
name|Exception
block|{
name|TextMessage
name|mockTextMessage
init|=
name|createMockTextMessage
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockTextMessage
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"myTextMessageContent"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockTextMessage
operator|.
name|getJMSDestination
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jmsDestination
argument_list|)
expr_stmt|;
name|JMSVendor
name|mockVendor
init|=
name|createMockVendor
argument_list|()
decl_stmt|;
name|String
name|toAddress
init|=
literal|"someToAddress"
decl_stmt|;
if|if
condition|(
name|jmsDestination
operator|!=
literal|null
condition|)
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|mockVendor
operator|.
name|toAddress
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Destination
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|toAddress
argument_list|)
expr_stmt|;
block|}
name|JMSMappingOutboundTransformer
name|transformer
init|=
operator|new
name|JMSMappingOutboundTransformer
argument_list|(
name|mockVendor
argument_list|)
decl_stmt|;
name|Message
name|amqp
init|=
name|transformer
operator|.
name|convert
argument_list|(
name|mockTextMessage
argument_list|)
decl_stmt|;
name|MessageAnnotations
name|ma
init|=
name|amqp
operator|.
name|getMessageAnnotations
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|maMap
init|=
name|ma
operator|==
literal|null
condition|?
literal|null
else|:
name|ma
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|maMap
operator|!=
literal|null
condition|)
block|{
name|Object
name|actualValue
init|=
name|maMap
operator|.
name|get
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-to-type"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected annotation value"
argument_list|,
name|expectedAnnotationValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedAnnotationValue
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Expected annotation value, but there were no annotations"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jmsDestination
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Unexpected 'to' address"
argument_list|,
name|toAddress
argument_list|,
name|amqp
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ======= JMSReplyTo Handling =========
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSReplyToNull
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestConvertMessageWithJMSReplyTo
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSReplyToQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|Queue
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Queue
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSReplyTo
argument_list|(
name|mockDest
argument_list|,
literal|"queue"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSReplyToTemporaryQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|TemporaryQueue
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TemporaryQueue
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSReplyTo
argument_list|(
name|mockDest
argument_list|,
literal|"temporary,queue"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSReplyToTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|Topic
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Topic
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSReplyTo
argument_list|(
name|mockDest
argument_list|,
literal|"topic"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertMessageWithJMSReplyToTemporaryTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|TemporaryTopic
name|mockDest
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TemporaryTopic
operator|.
name|class
argument_list|)
decl_stmt|;
name|doTestConvertMessageWithJMSReplyTo
argument_list|(
name|mockDest
argument_list|,
literal|"temporary,topic"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTestConvertMessageWithJMSReplyTo
parameter_list|(
name|Destination
name|jmsReplyTo
parameter_list|,
name|Object
name|expectedAnnotationValue
parameter_list|)
throws|throws
name|Exception
block|{
name|TextMessage
name|mockTextMessage
init|=
name|createMockTextMessage
argument_list|()
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockTextMessage
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"myTextMessageContent"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockTextMessage
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|jmsReplyTo
argument_list|)
expr_stmt|;
name|JMSVendor
name|mockVendor
init|=
name|createMockVendor
argument_list|()
decl_stmt|;
name|String
name|replyToAddress
init|=
literal|"someReplyToAddress"
decl_stmt|;
if|if
condition|(
name|jmsReplyTo
operator|!=
literal|null
condition|)
block|{
name|Mockito
operator|.
name|when
argument_list|(
name|mockVendor
operator|.
name|toAddress
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Destination
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|replyToAddress
argument_list|)
expr_stmt|;
block|}
name|JMSMappingOutboundTransformer
name|transformer
init|=
operator|new
name|JMSMappingOutboundTransformer
argument_list|(
name|mockVendor
argument_list|)
decl_stmt|;
name|Message
name|amqp
init|=
name|transformer
operator|.
name|convert
argument_list|(
name|mockTextMessage
argument_list|)
decl_stmt|;
name|MessageAnnotations
name|ma
init|=
name|amqp
operator|.
name|getMessageAnnotations
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|maMap
init|=
name|ma
operator|==
literal|null
condition|?
literal|null
else|:
name|ma
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|maMap
operator|!=
literal|null
condition|)
block|{
name|Object
name|actualValue
init|=
name|maMap
operator|.
name|get
argument_list|(
name|Symbol
operator|.
name|valueOf
argument_list|(
literal|"x-opt-reply-type"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Unexpected annotation value"
argument_list|,
name|expectedAnnotationValue
argument_list|,
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedAnnotationValue
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Expected annotation value, but there were no annotations"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jmsReplyTo
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Unexpected 'reply-to' address"
argument_list|,
name|replyToAddress
argument_list|,
name|amqp
operator|.
name|getReplyTo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ======= Utility Methods =========
specifier|private
name|TextMessage
name|createMockTextMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|TextMessage
name|mockTextMessage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|TextMessage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|mockTextMessage
operator|.
name|getPropertyNames
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
name|enumeration
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|mockTextMessage
return|;
block|}
specifier|private
name|JMSVendor
name|createMockVendor
parameter_list|()
block|{
name|JMSVendor
name|mockVendor
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|JMSVendor
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|mockVendor
return|;
block|}
block|}
end_class

end_unit

