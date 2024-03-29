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
name|stomp
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|Subscription
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
name|PolicyMap
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
class|class
name|StompPrefetchTest
extends|extends
name|StompTestSupport
block|{
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
name|StompPrefetchTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|applyBrokerPolicies
parameter_list|()
throws|throws
name|Exception
block|{
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setQueuePrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setTopicPrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setDurableTopicPrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setQueueBrowserPrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testTopicSubPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|connect
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|subscribe
argument_list|(
literal|"/topic/T"
argument_list|,
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|AUTO
argument_list|)
expr_stmt|;
name|verifyPrefetch
argument_list|(
literal|10
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"T"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testDurableSubPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|connect
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"durablesub"
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|subscribe
argument_list|(
literal|"/topic/T"
argument_list|,
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|AUTO
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|verifyPrefetch
argument_list|(
literal|10
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"T"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testQBrowserSubPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"login"
argument_list|,
literal|"system"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"passcode"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
literal|"aBrowser"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"browser"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"accept-version"
argument_list|,
literal|"1.1"
argument_list|)
expr_stmt|;
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|connect
argument_list|(
name|headers
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|subscribe
argument_list|(
literal|"/queue/Q"
argument_list|,
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|AUTO
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|verifyPrefetch
argument_list|(
literal|10
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testQueueSubPrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|stompConnect
argument_list|()
expr_stmt|;
name|stompConnection
operator|.
name|connect
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|stompConnection
operator|.
name|subscribe
argument_list|(
literal|"/queue/Q"
argument_list|,
name|Stomp
operator|.
name|Headers
operator|.
name|Subscribe
operator|.
name|AckModeValues
operator|.
name|AUTO
argument_list|)
expr_stmt|;
name|verifyPrefetch
argument_list|(
literal|10
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyPrefetch
parameter_list|(
specifier|final
name|int
name|val
parameter_list|,
specifier|final
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"success in time"
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
throws|throws
name|Exception
block|{
try|try
block|{
name|Subscription
name|sub
init|=
name|brokerService
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|ActiveMQDestination
operator|.
name|transform
argument_list|(
name|dest
argument_list|)
argument_list|)
operator|.
name|getConsumers
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"sub prefetch: "
operator|+
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getPrefetchSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|val
operator|==
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{                 }
return|return
literal|false
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|30
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

