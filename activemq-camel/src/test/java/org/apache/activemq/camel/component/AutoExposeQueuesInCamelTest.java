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
name|camel
operator|.
name|component
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
name|Collection
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
name|Endpoint
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
name|impl
operator|.
name|DefaultCamelContext
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
name|spi
operator|.
name|BrowsableEndpoint
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
name|util
operator|.
name|CamelContextHelper
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

begin_comment
comment|/**  * Shows that we can see the queues inside ActiveMQ via Camel  * by enabling the {@link ActiveMQComponent#setExposeAllQueues(boolean)} flag  *  *   */
end_comment

begin_class
specifier|public
class|class
name|AutoExposeQueuesInCamelTest
extends|extends
name|EmbeddedBrokerTestSupport
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
name|AutoExposeQueuesInCamelTest
operator|.
name|class
argument_list|)
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
name|CamelContext
name|camelContext
init|=
operator|new
name|DefaultCamelContext
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testWorks
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Looking for endpoints..."
argument_list|)
expr_stmt|;
comment|// Changed from using CamelContextHelper.getSingletonEndpoints here because JMS Endpoints in Camel
comment|// are always non-singleton
name|List
argument_list|<
name|BrowsableEndpoint
argument_list|>
name|endpoints
init|=
name|getEndpoints
argument_list|(
name|camelContext
argument_list|,
name|BrowsableEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|BrowsableEndpoint
name|endpoint
range|:
name|endpoints
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Endpoint: "
operator|+
name|endpoint
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Should have found an endpoint: "
operator|+
name|endpoints
argument_list|,
literal|1
argument_list|,
name|endpoints
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getEndpoints
parameter_list|(
name|CamelContext
name|camelContext
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Endpoint
argument_list|>
name|endpoints
init|=
name|camelContext
operator|.
name|getEndpoints
argument_list|()
decl_stmt|;
for|for
control|(
name|Endpoint
name|endpoint
range|:
name|endpoints
control|)
block|{
if|if
condition|(
name|type
operator|.
name|isInstance
argument_list|(
name|endpoint
argument_list|)
condition|)
block|{
name|T
name|value
init|=
name|type
operator|.
name|cast
argument_list|(
name|endpoint
argument_list|)
decl_stmt|;
name|answer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
annotation|@
name|Override
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
comment|// lets configure the ActiveMQ component for Camel
name|ActiveMQComponent
name|component
init|=
operator|new
name|ActiveMQComponent
argument_list|()
decl_stmt|;
name|component
operator|.
name|setBrokerURL
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|component
operator|.
name|setExposeAllQueues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|addComponent
argument_list|(
literal|"activemq"
argument_list|,
name|component
argument_list|)
expr_stmt|;
name|camelContext
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|camelContext
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
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
block|}
end_class

end_unit

