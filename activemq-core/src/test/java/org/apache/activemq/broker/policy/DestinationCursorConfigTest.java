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
name|policy
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
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
name|policy
operator|.
name|PendingQueueMessageStoragePolicy
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
name|policy
operator|.
name|PendingSubscriberMessageStoragePolicy
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
name|policy
operator|.
name|PolicyEntry
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
name|policy
operator|.
name|VMPendingQueueMessageStoragePolicy
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
name|policy
operator|.
name|VMPendingSubscriberMessageStoragePolicy
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
name|xbean
operator|.
name|BrokerFactoryBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|DestinationCursorConfigTest
extends|extends
name|TestSupport
block|{
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
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
name|broker
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
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
operator|new
name|ClassPathResource
argument_list|(
literal|"org/apache/activemq/broker/policy/cursor.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|answer
init|=
name|factory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|testQueueConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|topic
operator|=
literal|false
expr_stmt|;
name|ActiveMQDestination
name|destination
init|=
operator|(
name|ActiveMQDestination
operator|)
name|createDestination
argument_list|(
literal|"org.apache.foo"
argument_list|)
decl_stmt|;
name|PolicyEntry
name|entry
init|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|PendingQueueMessageStoragePolicy
name|policy
init|=
name|entry
operator|.
name|getPendingQueuePolicy
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Policy is: "
operator|+
name|policy
argument_list|,
name|policy
operator|instanceof
name|VMPendingQueueMessageStoragePolicy
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTopicConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|topic
operator|=
literal|true
expr_stmt|;
name|ActiveMQDestination
name|destination
init|=
operator|(
name|ActiveMQDestination
operator|)
name|createDestination
argument_list|(
literal|"org.apache.foo"
argument_list|)
decl_stmt|;
name|PolicyEntry
name|entry
init|=
name|broker
operator|.
name|getDestinationPolicy
argument_list|()
operator|.
name|getEntryFor
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|PendingSubscriberMessageStoragePolicy
name|policy
init|=
name|entry
operator|.
name|getPendingSubscriberPolicy
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|entry
operator|.
name|isProducerFlowControl
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getMemoryLimit
argument_list|()
operator|==
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"subscriberPolicy is: "
operator|+
name|policy
argument_list|,
name|policy
operator|instanceof
name|VMPendingSubscriberMessageStoragePolicy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

