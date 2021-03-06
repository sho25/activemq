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
name|network
package|;
end_package

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
name|assertTrue
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
name|TemporaryQueue
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
name|DuplexNetworkTest
extends|extends
name|SimpleNetworkTest
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
name|DuplexNetworkTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|String
name|getLocalBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/duplexLocalBroker.xml"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
literal|"remoteBroker"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61617?transport.connectAttemptTimeout=2000"
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTempQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|TemporaryQueue
name|temp
init|=
name|localSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|temp
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|localSession
operator|.
name|createTextMessage
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Destination not created"
argument_list|,
literal|1
argument_list|,
name|remoteBroker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|temp
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Destination not deleted"
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
return|return
literal|0
operator|==
name|remoteBroker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStaysUp
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|bridgeIdentity
init|=
name|getBridgeId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Bridges: "
operator|+
name|bridgeIdentity
argument_list|)
expr_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Same bridges"
argument_list|,
name|bridgeIdentity
argument_list|,
name|getBridgeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|getBridgeId
parameter_list|()
block|{
name|int
name|id
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|id
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|id
operator|=
name|localBroker
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|activeBridges
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|tryAgainInABit
parameter_list|)
block|{
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{                 }
block|}
block|}
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|assertNetworkBridgeStatistics
parameter_list|(
specifier|final
name|long
name|expectedLocalSent
parameter_list|,
specifier|final
name|long
name|expectedRemoteSent
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|NetworkBridge
name|localBridge
init|=
name|localBroker
operator|.
name|getNetworkConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|activeBridges
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
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
return|return
name|expectedLocalSent
operator|==
name|localBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|&&
name|expectedRemoteSent
operator|==
name|localBridge
operator|.
name|getNetworkBridgeStatistics
argument_list|()
operator|.
name|getReceivedCount
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

