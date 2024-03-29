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
name|util
operator|.
name|oxm
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ActiveMQConnectionFactory
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
name|ActiveMQMessageConsumer
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
name|MessageTransformer
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
name|xstream
operator|.
name|SamplePojo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|oxm
operator|.
name|AbstractXMLMessageTransformer
operator|.
name|MessageTransform
operator|.
name|ADAPTIVE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|oxm
operator|.
name|AbstractXMLMessageTransformer
operator|.
name|MessageTransform
operator|.
name|OBJECT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|oxm
operator|.
name|AbstractXMLMessageTransformer
operator|.
name|MessageTransform
operator|.
name|XML
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractXMLMessageTransformerTest
extends|extends
name|TestCase
block|{
specifier|protected
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|long
name|timeout
init|=
literal|5000
decl_stmt|;
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|MessageTransformer
name|transformer
parameter_list|)
throws|throws
name|Exception
block|{
name|connectionFactory
operator|.
name|setTransformer
argument_list|(
name|transformer
argument_list|)
expr_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|protected
specifier|abstract
name|AbstractXMLMessageTransformer
name|createTransformer
parameter_list|()
function_decl|;
specifier|public
name|void
name|testSendObjectMessageReceiveAsTextMessageAndObjectMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractXMLMessageTransformer
name|transformer
init|=
name|createTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setTransformType
argument_list|(
name|XML
argument_list|)
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|(
name|transformer
argument_list|)
expr_stmt|;
comment|// lets create the consumers
name|Session
name|objectSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|objectSession
operator|.
name|createTopic
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|objectConsumer
init|=
name|objectSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Session
name|textSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|textConsumer
init|=
name|textSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// lets clear the transformer on this consumer so we see the message as
comment|// it really is
operator|(
operator|(
name|ActiveMQMessageConsumer
operator|)
name|textConsumer
operator|)
operator|.
name|setTransformer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// send a message
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|ObjectMessage
name|request
init|=
name|producerSession
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|SamplePojo
argument_list|(
literal|"James"
argument_list|,
literal|"London"
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// lets consume it as an object message
name|Message
name|message
init|=
name|objectConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be an ObjectMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|ObjectMessage
argument_list|)
expr_stmt|;
name|ObjectMessage
name|objectMessage
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
name|Object
name|object
init|=
name|objectMessage
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"object payload of wrong type: "
operator|+
name|object
argument_list|,
name|object
operator|instanceof
name|SamplePojo
argument_list|)
expr_stmt|;
name|SamplePojo
name|body
init|=
operator|(
name|SamplePojo
operator|)
name|object
decl_stmt|;
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
literal|"James"
argument_list|,
name|body
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"city"
argument_list|,
literal|"London"
argument_list|,
name|body
operator|.
name|getCity
argument_list|()
argument_list|)
expr_stmt|;
comment|// lets consume it as a text message
name|message
operator|=
name|textConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be a TextMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|String
name|text
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Text should be non-empty!"
argument_list|,
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received XML..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendTextMessageReceiveAsObjectMessageAndTextMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractXMLMessageTransformer
name|transformer
init|=
name|createTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setTransformType
argument_list|(
name|OBJECT
argument_list|)
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|(
name|transformer
argument_list|)
expr_stmt|;
comment|// lets create the consumers
name|Session
name|textSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|textSession
operator|.
name|createTopic
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|textConsumer
init|=
name|textSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Session
name|objectSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|objectConsumer
init|=
name|objectSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// lets clear the transformer on this consumer so we see the message as
comment|// it really is
operator|(
operator|(
name|ActiveMQMessageConsumer
operator|)
name|objectConsumer
operator|)
operator|.
name|setTransformer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// send a message
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|String
name|xmlText
init|=
literal|"<org.apache.activemq.util.xstream.SamplePojo>"
operator|+
literal|"<name>James</name>"
operator|+
literal|"<city>London</city>"
operator|+
literal|"</org.apache.activemq.util.xstream.SamplePojo>"
decl_stmt|;
name|TextMessage
name|request
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
name|xmlText
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|Message
name|message
decl_stmt|;
comment|// lets consume it as a text message
name|message
operator|=
name|textConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be a TextMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|String
name|text
init|=
name|textMessage
operator|.
name|getText
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Text should be non-empty!"
argument_list|,
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// lets consume it as an object message
name|message
operator|=
name|objectConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be an ObjectMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|ObjectMessage
argument_list|)
expr_stmt|;
name|ObjectMessage
name|objectMessage
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
name|Object
name|object
init|=
name|objectMessage
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"object payload of wrong type: "
operator|+
name|object
argument_list|,
name|object
operator|instanceof
name|SamplePojo
argument_list|)
expr_stmt|;
name|SamplePojo
name|body
init|=
operator|(
name|SamplePojo
operator|)
name|object
decl_stmt|;
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
literal|"James"
argument_list|,
name|body
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"city"
argument_list|,
literal|"London"
argument_list|,
name|body
operator|.
name|getCity
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAdaptiveTransform
parameter_list|()
throws|throws
name|Exception
block|{
name|AbstractXMLMessageTransformer
name|transformer
init|=
name|createTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setTransformType
argument_list|(
name|ADAPTIVE
argument_list|)
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|(
name|transformer
argument_list|)
expr_stmt|;
comment|// lets create the consumers
name|Session
name|adaptiveSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Destination
name|destination
init|=
name|adaptiveSession
operator|.
name|createTopic
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageConsumer
name|adaptiveConsumer
init|=
name|adaptiveSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Session
name|origSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageConsumer
name|origConsumer
init|=
name|origSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// lets clear the transformer on this consumer so we see the message as
comment|// it really is
operator|(
operator|(
name|ActiveMQMessageConsumer
operator|)
name|origConsumer
operator|)
operator|.
name|setTransformer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// Create producer
name|Session
name|producerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|producerSession
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Message
name|message
decl_stmt|;
name|ObjectMessage
name|objectMessage
decl_stmt|;
name|TextMessage
name|textMessage
decl_stmt|;
name|SamplePojo
name|body
decl_stmt|;
name|Object
name|object
decl_stmt|;
name|String
name|text
decl_stmt|;
comment|// Send a text message
name|String
name|xmlText
init|=
literal|"<org.apache.activemq.util.xstream.SamplePojo>"
operator|+
literal|"<name>James</name>"
operator|+
literal|"<city>London</city>"
operator|+
literal|"</org.apache.activemq.util.xstream.SamplePojo>"
decl_stmt|;
name|TextMessage
name|txtRequest
init|=
name|producerSession
operator|.
name|createTextMessage
argument_list|(
name|xmlText
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|txtRequest
argument_list|)
expr_stmt|;
comment|// lets consume it as a text message
name|message
operator|=
name|adaptiveConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be a TextMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|textMessage
operator|=
operator|(
name|TextMessage
operator|)
name|message
expr_stmt|;
name|text
operator|=
name|textMessage
operator|.
name|getText
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text should be non-empty!"
argument_list|,
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
comment|// lets consume it as an object message
name|message
operator|=
name|origConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be an ObjectMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|ObjectMessage
argument_list|)
expr_stmt|;
name|objectMessage
operator|=
operator|(
name|ObjectMessage
operator|)
name|message
expr_stmt|;
name|object
operator|=
name|objectMessage
operator|.
name|getObject
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"object payload of wrong type: "
operator|+
name|object
argument_list|,
name|object
operator|instanceof
name|SamplePojo
argument_list|)
expr_stmt|;
name|body
operator|=
operator|(
name|SamplePojo
operator|)
name|object
expr_stmt|;
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
literal|"James"
argument_list|,
name|body
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"city"
argument_list|,
literal|"London"
argument_list|,
name|body
operator|.
name|getCity
argument_list|()
argument_list|)
expr_stmt|;
comment|// Send object message
name|ObjectMessage
name|objRequest
init|=
name|producerSession
operator|.
name|createObjectMessage
argument_list|(
operator|new
name|SamplePojo
argument_list|(
literal|"James"
argument_list|,
literal|"London"
argument_list|)
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|objRequest
argument_list|)
expr_stmt|;
comment|// lets consume it as an object message
name|message
operator|=
name|adaptiveConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be an ObjectMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|ObjectMessage
argument_list|)
expr_stmt|;
name|objectMessage
operator|=
operator|(
name|ObjectMessage
operator|)
name|message
expr_stmt|;
name|object
operator|=
name|objectMessage
operator|.
name|getObject
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"object payload of wrong type: "
operator|+
name|object
argument_list|,
name|object
operator|instanceof
name|SamplePojo
argument_list|)
expr_stmt|;
name|body
operator|=
operator|(
name|SamplePojo
operator|)
name|object
expr_stmt|;
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
literal|"James"
argument_list|,
name|body
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"city"
argument_list|,
literal|"London"
argument_list|,
name|body
operator|.
name|getCity
argument_list|()
argument_list|)
expr_stmt|;
comment|// lets consume it as a text message
name|message
operator|=
name|origConsumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Should have received a message!"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be a TextMessage but was: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|textMessage
operator|=
operator|(
name|TextMessage
operator|)
name|message
expr_stmt|;
name|text
operator|=
name|textMessage
operator|.
name|getText
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Text should be non-empty!"
argument_list|,
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Received XML..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

