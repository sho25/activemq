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
name|xbean
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|broker
operator|.
name|Broker
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
name|BrokerFactory
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
name|ConnectionContext
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
name|Topic
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
name|DispatchPolicy
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
name|LastImageSubscriptionRecoveryPolicy
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
name|RoundRobinDispatchPolicy
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
name|StrictOrderDispatchPolicy
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
name|SubscriptionRecoveryPolicy
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
name|TimedSubscriptionRecoveryPolicy
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
name|command
operator|.
name|ConnectionId
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
name|ConnectionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|XBeanConfigTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|XBeanConfigTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
name|Broker
name|broker
decl_stmt|;
specifier|protected
name|ConnectionContext
name|context
decl_stmt|;
specifier|protected
name|ConnectionInfo
name|info
decl_stmt|;
specifier|public
name|void
name|testBrokerConfiguredCorrectly
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Validate the system properties are being evaluated in xbean.
name|assertEquals
argument_list|(
literal|"testbroker"
argument_list|,
name|brokerService
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|broker
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"FOO.BAR"
argument_list|)
argument_list|)
decl_stmt|;
name|DispatchPolicy
name|dispatchPolicy
init|=
name|topic
operator|.
name|getDispatchPolicy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"dispatchPolicy should be RoundRobinDispatchPolicy: "
operator|+
name|dispatchPolicy
argument_list|,
name|dispatchPolicy
operator|instanceof
name|RoundRobinDispatchPolicy
argument_list|)
expr_stmt|;
name|SubscriptionRecoveryPolicy
name|subscriptionRecoveryPolicy
init|=
name|topic
operator|.
name|getSubscriptionRecoveryPolicy
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"subscriptionRecoveryPolicy should be LastImageSubscriptionRecoveryPolicy: "
operator|+
name|subscriptionRecoveryPolicy
argument_list|,
name|subscriptionRecoveryPolicy
operator|instanceof
name|LastImageSubscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"destination: "
operator|+
name|topic
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"dispatchPolicy: "
operator|+
name|dispatchPolicy
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"subscriptionRecoveryPolicy: "
operator|+
name|subscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
name|topic
operator|=
operator|(
name|Topic
operator|)
name|broker
operator|.
name|addDestination
argument_list|(
name|context
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"ORDERS.BOOKS"
argument_list|)
argument_list|)
expr_stmt|;
name|dispatchPolicy
operator|=
name|topic
operator|.
name|getDispatchPolicy
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"dispatchPolicy should be StrictOrderDispatchPolicy: "
operator|+
name|dispatchPolicy
argument_list|,
name|dispatchPolicy
operator|instanceof
name|StrictOrderDispatchPolicy
argument_list|)
expr_stmt|;
name|subscriptionRecoveryPolicy
operator|=
name|topic
operator|.
name|getSubscriptionRecoveryPolicy
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"subscriptionRecoveryPolicy should be TimedSubscriptionRecoveryPolicy: "
operator|+
name|subscriptionRecoveryPolicy
argument_list|,
name|subscriptionRecoveryPolicy
operator|instanceof
name|TimedSubscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
name|TimedSubscriptionRecoveryPolicy
name|timedSubcriptionPolicy
init|=
operator|(
name|TimedSubscriptionRecoveryPolicy
operator|)
name|subscriptionRecoveryPolicy
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getRecoverDuration()"
argument_list|,
literal|60000
argument_list|,
name|timedSubcriptionPolicy
operator|.
name|getRecoverDuration
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"destination: "
operator|+
name|topic
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"dispatchPolicy: "
operator|+
name|dispatchPolicy
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"subscriptionRecoveryPolicy: "
operator|+
name|subscriptionRecoveryPolicy
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"brokername"
argument_list|,
literal|"testbroker"
argument_list|)
expr_stmt|;
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|=
name|brokerService
operator|.
name|getBroker
argument_list|()
expr_stmt|;
comment|// started automatically
comment|// brokerService.start();
name|context
operator|=
operator|new
name|ConnectionContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|info
operator|=
operator|new
name|ConnectionInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
literal|"James"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setUserName
argument_list|(
literal|"James"
argument_list|)
expr_stmt|;
name|info
operator|.
name|setConnectionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
literal|"1234"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
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
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"No broker created!"
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
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
literal|"org/apache/activemq/xbean/activemq-policy.xml"
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loading broker configuration from the classpath with URI: "
operator|+
name|uri
argument_list|)
expr_stmt|;
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"xbean:"
operator|+
name|uri
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

