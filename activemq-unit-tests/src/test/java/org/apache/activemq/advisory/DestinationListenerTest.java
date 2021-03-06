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
name|advisory
package|;
end_package

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
name|List
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
name|MessageProducer
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
name|EmbeddedBrokerTestSupport
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
name|BrokerService
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
name|ActiveMQTopic
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|DestinationListenerTest
extends|extends
name|EmbeddedBrokerTestSupport
implements|implements
name|DestinationListener
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
name|DestinationListenerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|protected
name|ActiveMQQueue
name|sampleQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"foo.bar"
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQTopic
name|sampleTopic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"cheese"
argument_list|)
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|newDestinations
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testDestiationSourceHasInitialDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|DestinationSource
name|destinationSource
init|=
name|connection
operator|.
name|getDestinationSource
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ActiveMQQueue
argument_list|>
name|queues
init|=
name|destinationSource
operator|.
name|getQueues
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|ActiveMQTopic
argument_list|>
name|topics
init|=
name|destinationSource
operator|.
name|getTopics
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Queues: "
operator|+
name|queues
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Topics: "
operator|+
name|topics
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The queues should not be empty!"
argument_list|,
operator|!
name|queues
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The topics should not be empty!"
argument_list|,
operator|!
name|topics
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"queues contains initial queue: "
operator|+
name|queues
argument_list|,
name|queues
operator|.
name|contains
argument_list|(
name|sampleQueue
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"topics contains initial topic: "
operator|+
name|queues
argument_list|,
name|topics
operator|.
name|contains
argument_list|(
name|sampleTopic
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConsumerForcesNotificationOfNewDestination
parameter_list|()
throws|throws
name|Exception
block|{
comment|// now lets cause a destination to be created
name|Session
name|session
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
name|ActiveMQQueue
name|newQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test.Cheese"
argument_list|)
decl_stmt|;
name|session
operator|.
name|createConsumer
argument_list|(
name|newQueue
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newQueue
argument_list|,
name|isIn
argument_list|(
name|newDestinations
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"New destinations are: "
operator|+
name|newDestinations
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testProducerForcesNotificationOfNewDestination
parameter_list|()
throws|throws
name|Exception
block|{
comment|// now lets cause a destination to be created
name|Session
name|session
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
name|ActiveMQQueue
name|newQueue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test.Beer"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|newQueue
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"<hello>world</hello>"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newQueue
argument_list|,
name|isIn
argument_list|(
name|newDestinations
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"New destinations are: "
operator|+
name|newDestinations
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onDestinationEvent
parameter_list|(
name|DestinationEvent
name|event
parameter_list|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|event
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|isAddOperation
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Added:   "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|newDestinations
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Removed: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|newDestinations
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
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
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|.
name|getDestinationSource
argument_list|()
operator|.
name|setDestinationListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
name|super
operator|.
name|createBroker
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setDestinations
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
name|sampleQueue
block|,
name|sampleTopic
block|}
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

