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
name|java
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
name|assertSame
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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|concurrent
operator|.
name|TimeUnit
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
name|AbstractVirtualDestTest
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
name|BrokerPlugin
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
name|broker
operator|.
name|region
operator|.
name|DestinationInterceptor
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
name|region
operator|.
name|virtual
operator|.
name|CompositeQueue
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
name|region
operator|.
name|virtual
operator|.
name|FilteredDestination
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
name|region
operator|.
name|virtual
operator|.
name|VirtualDestination
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
name|region
operator|.
name|virtual
operator|.
name|VirtualDestinationInterceptor
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
name|region
operator|.
name|virtual
operator|.
name|VirtualTopic
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
name|plugin
operator|.
name|java
operator|.
name|JavaRuntimeConfigurationBroker
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
name|plugin
operator|.
name|java
operator|.
name|JavaRuntimeConfigurationPlugin
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
name|Wait
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

begin_class
specifier|public
class|class
name|JavaVirtualDestTest
extends|extends
name|AbstractVirtualDestTest
block|{
specifier|public
specifier|static
specifier|final
name|int
name|SLEEP
init|=
literal|2
decl_stmt|;
comment|// seconds
specifier|private
name|JavaRuntimeConfigurationBroker
name|javaConfigBroker
decl_stmt|;
specifier|public
name|void
name|startBroker
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
name|brokerService
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|JavaRuntimeConfigurationPlugin
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|javaConfigBroker
operator|=
operator|(
name|JavaRuntimeConfigurationBroker
operator|)
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|JavaRuntimeConfigurationBroker
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNew
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
operator|new
name|BrokerService
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
comment|// default config has support for VirtualTopic.>
name|DestinationInterceptor
index|[]
name|interceptors
init|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"it is virtual topic interceptor"
argument_list|,
name|interceptors
index|[
literal|0
index|]
operator|instanceof
name|VirtualDestinationInterceptor
argument_list|)
expr_stmt|;
name|VirtualDestinationInterceptor
name|defaultValue
init|=
operator|(
name|VirtualDestinationInterceptor
operator|)
name|interceptors
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"default names in place"
argument_list|,
literal|"VirtualTopic.>"
argument_list|,
name|defaultValue
operator|.
name|getVirtualDestinations
argument_list|()
index|[
literal|0
index|]
operator|.
name|getVirtualDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"VirtualTopic.Default"
argument_list|)
expr_stmt|;
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"it is virtual topic interceptor"
argument_list|,
name|interceptors
index|[
literal|0
index|]
operator|instanceof
name|VirtualDestinationInterceptor
argument_list|)
expr_stmt|;
comment|// update will happen on addDestination
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
name|VirtualDestinationInterceptor
name|newValue
init|=
operator|(
name|VirtualDestinationInterceptor
operator|)
name|interceptors
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"new names in place"
argument_list|,
literal|"A.>"
argument_list|,
name|defaultValue
operator|.
name|getVirtualDestinations
argument_list|()
index|[
literal|0
index|]
operator|.
name|getVirtualDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
comment|// apply again - ensure no change
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"same instance"
argument_list|,
name|newValue
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewComposite
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
operator|new
name|BrokerService
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|CompositeQueue
name|queue
init|=
name|buildCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"VirtualDestination.QueueConsumer"
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualDestination.TopicConsumer"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|queue
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewCompositeApplyImmediately
parameter_list|()
throws|throws
name|Exception
block|{
name|startBroker
argument_list|(
operator|new
name|BrokerService
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|CompositeQueue
name|queue
init|=
name|buildCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"VirtualDestination.QueueConsumer"
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualDestination.TopicConsumer"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|queue
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModComposite
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|CompositeQueue
name|queue
init|=
name|buildCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"VirtualDestination.QueueConsumer"
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualDestination.TopicConsumer"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|buildInterceptor
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|queue
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|exerciseCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|)
expr_stmt|;
comment|//Apply updated config
name|CompositeQueue
name|newConfig
init|=
name|buildCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"VirtualDestination.QueueConsumer"
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualDestination.TopicConsumer"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|newConfig
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|)
expr_stmt|;
name|exerciseCompositeQueue
argument_list|(
literal|"VirtualDestination.CompositeQueue"
argument_list|,
literal|"VirtualDestination.CompositeQueue"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewNoDefaultVirtualTopicSupport
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setUseVirtualTopics
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|DestinationInterceptor
index|[]
name|interceptors
init|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|0
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
comment|// update will happen on addDestination
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
name|interceptors
operator|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"it is virtual topic interceptor"
argument_list|,
name|interceptors
index|[
literal|0
index|]
operator|instanceof
name|VirtualDestinationInterceptor
argument_list|)
expr_stmt|;
comment|//apply new config again, make sure still just 1 interceptor
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
comment|// update will happen on addDestination
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
name|interceptors
operator|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"it is virtual topic interceptor"
argument_list|,
name|interceptors
index|[
literal|0
index|]
operator|instanceof
name|VirtualDestinationInterceptor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewWithMirrorQueueSupport
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setUseMirroredQueues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|DestinationInterceptor
index|[]
name|interceptors
init|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"expected interceptor"
argument_list|,
literal|2
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
comment|// update will happen on addDestination
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
name|interceptors
operator|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected interceptor"
argument_list|,
literal|2
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"it is virtual topic interceptor"
argument_list|,
name|interceptors
index|[
literal|0
index|]
operator|instanceof
name|VirtualDestinationInterceptor
argument_list|)
expr_stmt|;
name|VirtualDestinationInterceptor
name|newValue
init|=
operator|(
name|VirtualDestinationInterceptor
operator|)
name|interceptors
index|[
literal|0
index|]
decl_stmt|;
comment|// apply again - ensure no change
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
literal|"same instance"
argument_list|,
name|newValue
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|buildInterceptor
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|DestinationInterceptor
index|[]
name|interceptors
init|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"it is virtual topic interceptor"
argument_list|,
name|interceptors
index|[
literal|0
index|]
operator|instanceof
name|VirtualDestinationInterceptor
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
name|VirtualDestinationInterceptor
name|defaultValue
init|=
operator|(
name|VirtualDestinationInterceptor
operator|)
name|interceptors
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|"configured names in place"
argument_list|,
literal|"A.>"
argument_list|,
name|defaultValue
operator|.
name|getVirtualDestinations
argument_list|()
index|[
literal|0
index|]
operator|.
name|getVirtualDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
comment|//apply empty config - this removes all virtual destinations from the interceptor
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
comment|// update will happen on addDestination
name|forceAddDestination
argument_list|(
literal|"AnyDest"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"getVirtualDestinations empty on time"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
block|{
return|return
literal|0
operator|==
operator|(
operator|(
name|VirtualDestinationInterceptor
operator|)
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
index|[
literal|0
index|]
operator|)
operator|.
name|getVirtualDestinations
argument_list|()
operator|.
name|length
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
comment|// reverse the remove, add again
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
comment|// update will happen on addDestination
name|exerciseVirtualTopic
argument_list|(
literal|"A.NewOne"
argument_list|)
expr_stmt|;
name|interceptors
operator|=
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"expected interceptor"
argument_list|,
literal|1
argument_list|,
name|interceptors
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"it is virtual topic interceptor"
argument_list|,
name|interceptors
index|[
literal|0
index|]
operator|instanceof
name|VirtualDestinationInterceptor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMod
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|buildInterceptor
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"B.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"B.Default"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"still one interceptor"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModApplyImmediately
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|buildInterceptor
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"B.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"B.Default"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"still one interceptor"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModWithMirroredQueue
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setUseMirroredQueues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|buildInterceptor
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"A.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one interceptor"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"A.Default"
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|buildVirtualTopic
argument_list|(
literal|"B.>"
argument_list|,
literal|false
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseVirtualTopic
argument_list|(
literal|"B.Default"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"still one interceptor"
argument_list|,
literal|1
argument_list|,
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNewFilteredComposite
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|FilteredDestination
name|filteredDestination
init|=
operator|new
name|FilteredDestination
argument_list|()
decl_stmt|;
name|filteredDestination
operator|.
name|setSelector
argument_list|(
literal|"odd = 'yes'"
argument_list|)
expr_stmt|;
name|filteredDestination
operator|.
name|setQueue
argument_list|(
literal|"VirtualDestination.QueueConsumer"
argument_list|)
expr_stmt|;
name|CompositeQueue
name|queue
init|=
name|buildCompositeQueue
argument_list|(
literal|"VirtualDestination.FilteredCompositeQueue"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|filteredDestination
argument_list|)
argument_list|)
decl_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|queue
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseFilteredCompositeQueue
argument_list|(
literal|"VirtualDestination.FilteredCompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModFilteredComposite
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|FilteredDestination
name|filteredDestination
init|=
operator|new
name|FilteredDestination
argument_list|()
decl_stmt|;
name|filteredDestination
operator|.
name|setSelector
argument_list|(
literal|"odd = 'yes'"
argument_list|)
expr_stmt|;
name|filteredDestination
operator|.
name|setQueue
argument_list|(
literal|"VirtualDestination.QueueConsumer"
argument_list|)
expr_stmt|;
name|CompositeQueue
name|queue
init|=
name|buildCompositeQueue
argument_list|(
literal|"VirtualDestination.FilteredCompositeQueue"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|filteredDestination
argument_list|)
argument_list|)
decl_stmt|;
name|brokerService
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|buildInterceptor
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|queue
block|}
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerService
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"broker alive"
argument_list|,
name|brokerService
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
name|exerciseFilteredCompositeQueue
argument_list|(
literal|"VirtualDestination.FilteredCompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|filteredDestination
operator|=
operator|new
name|FilteredDestination
argument_list|()
expr_stmt|;
name|filteredDestination
operator|.
name|setSelector
argument_list|(
literal|"odd = 'no'"
argument_list|)
expr_stmt|;
name|filteredDestination
operator|.
name|setQueue
argument_list|(
literal|"VirtualDestination.QueueConsumer"
argument_list|)
expr_stmt|;
name|queue
operator|=
name|buildCompositeQueue
argument_list|(
literal|"VirtualDestination.FilteredCompositeQueue"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|filteredDestination
argument_list|)
argument_list|)
expr_stmt|;
comment|//apply new config
name|javaConfigBroker
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|queue
block|}
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
name|SLEEP
argument_list|)
expr_stmt|;
name|exerciseFilteredCompositeQueue
argument_list|(
literal|"VirtualDestination.FilteredCompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|exerciseFilteredCompositeQueue
argument_list|(
literal|"VirtualDestination.FilteredCompositeQueue"
argument_list|,
literal|"VirtualDestination.QueueConsumer"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
name|CompositeQueue
name|buildCompositeQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|?
argument_list|>
name|forwardTo
parameter_list|)
block|{
return|return
name|buildCompositeQueue
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|forwardTo
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|CompositeQueue
name|buildCompositeQueue
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|forwardOnly
parameter_list|,
name|Collection
argument_list|<
name|?
argument_list|>
name|forwardTo
parameter_list|)
block|{
name|CompositeQueue
name|queue
init|=
operator|new
name|CompositeQueue
argument_list|()
decl_stmt|;
name|queue
operator|.
name|setForwardOnly
argument_list|(
name|forwardOnly
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setForwardTo
argument_list|(
name|forwardTo
argument_list|)
expr_stmt|;
return|return
name|queue
return|;
block|}
specifier|protected
specifier|static
name|VirtualTopic
name|buildVirtualTopic
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|selectorAware
parameter_list|)
block|{
name|VirtualTopic
name|virtualTopic
init|=
operator|new
name|VirtualTopic
argument_list|()
decl_stmt|;
name|virtualTopic
operator|.
name|setSelectorAware
argument_list|(
name|selectorAware
argument_list|)
expr_stmt|;
name|virtualTopic
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|virtualTopic
return|;
block|}
specifier|protected
specifier|static
name|VirtualDestinationInterceptor
name|buildInterceptor
parameter_list|(
name|VirtualDestination
index|[]
name|virtualDestinations
parameter_list|)
block|{
name|VirtualDestinationInterceptor
name|virtualDestinationInterceptor
init|=
operator|new
name|VirtualDestinationInterceptor
argument_list|()
decl_stmt|;
name|virtualDestinationInterceptor
operator|.
name|setVirtualDestinations
argument_list|(
name|virtualDestinations
argument_list|)
expr_stmt|;
return|return
name|virtualDestinationInterceptor
return|;
block|}
block|}
end_class

end_unit

