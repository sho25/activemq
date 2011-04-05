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
name|camel
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnection
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
name|RedeliveryPolicy
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
name|camel
operator|.
name|CamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Handler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|RecipientList
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|annotation
operator|.
name|Autowired
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|test
operator|.
name|context
operator|.
name|ContextConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|test
operator|.
name|context
operator|.
name|junit38
operator|.
name|AbstractJUnit38SpringContextTests
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
annotation|@
name|ContextConfiguration
specifier|public
class|class
name|CamelRedeliveryTest
extends|extends
name|AbstractJUnit38SpringContextTests
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CamelRedeliveryTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Autowired
specifier|protected
name|CamelContext
name|camelContext
decl_stmt|;
specifier|public
name|void
name|testRedeliveryViaCamel
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
name|applicationContext
operator|.
name|getBean
argument_list|(
literal|"connectionFactory"
argument_list|,
name|ActiveMQConnectionFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
comment|// send message to dlq immediately
name|RedeliveryPolicy
name|policy
init|=
name|connection
operator|.
name|getRedeliveryPolicy
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|ActiveMQQueue
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"camelRedeliveryQ"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// Send the messages
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"1st"
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sent 1st message"
argument_list|)
expr_stmt|;
name|TextMessage
name|m
decl_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received and rolledback 1st message: "
operator|+
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"no immediate redelivery"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"received redelivery on second wait attempt, message: "
operator|+
name|m
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"got redelivery on second attempt"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text matches original"
argument_list|,
literal|"1st"
argument_list|,
name|m
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
comment|// came from camel
name|assertTrue
argument_list|(
literal|"redelivery marker header set, so came from camel"
argument_list|,
name|m
operator|.
name|getBooleanProperty
argument_list|(
literal|"CamelRedeliveryMarker"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|DestinationExtractor
block|{
annotation|@
name|RecipientList
annotation|@
name|Handler
specifier|public
name|String
name|routeTo
parameter_list|(
name|ActiveMQMessage
name|body
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|originalDestination
init|=
name|body
operator|.
name|getOriginalDestination
argument_list|()
decl_stmt|;
return|return
literal|"activemq:"
operator|+
name|originalDestination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|"?explicitQosEnabled=true&messageConverter=#messageConverter"
return|;
block|}
block|}
block|}
end_class

end_unit

