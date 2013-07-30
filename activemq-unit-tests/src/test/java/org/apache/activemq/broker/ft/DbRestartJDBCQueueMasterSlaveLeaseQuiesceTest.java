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
name|ft
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
name|store
operator|.
name|jdbc
operator|.
name|JDBCIOExceptionHandler
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
name|DbRestartJDBCQueueMasterSlaveLeaseQuiesceTest
extends|extends
name|DbRestartJDBCQueueMasterSlaveLeaseTest
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
name|DbRestartJDBCQueueMasterSlaveLeaseQuiesceTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|restartDelay
init|=
literal|500
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
comment|// master and slave survive db restart and retain master/slave status
name|JDBCIOExceptionHandler
name|stopConnectors
init|=
operator|new
name|JDBCIOExceptionHandler
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setIoExceptionHandler
argument_list|(
name|stopConnectors
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|delayTillRestartRequired
parameter_list|()
block|{
if|if
condition|(
name|restartDelay
operator|>
literal|500
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"delay for more than lease quantum. While Db is offline, master should stay alive but could loose lease"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"delay for less than lease quantum. While Db is offline, master should stay alive"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|restartDelay
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|verifyExpectedBroker
parameter_list|(
name|int
name|inflightMessageCount
parameter_list|)
block|{
if|if
condition|(
name|inflightMessageCount
operator|==
literal|0
operator|||
operator|(
name|inflightMessageCount
operator|==
name|failureCount
operator|+
literal|10
operator|&&
name|restartDelay
operator|<=
literal|500
operator|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"connected to master"
argument_list|,
name|master
operator|.
name|getBrokerName
argument_list|()
argument_list|,
operator|(
operator|(
name|ActiveMQConnection
operator|)
name|sendConnection
operator|)
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// lease expired while DB was offline, either or master/slave can grab it so assert is not deterministic
comment|// but we still need to validate sent == received
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|restartDelay
operator|=
literal|500
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testSendReceiveWithLeaseExpiry
parameter_list|()
throws|throws
name|Exception
block|{
name|restartDelay
operator|=
literal|3000
expr_stmt|;
name|testSendReceive
argument_list|()
expr_stmt|;
block|}
comment|// ignore this test case
specifier|public
name|void
name|testAdvisory
parameter_list|()
throws|throws
name|Exception
block|{}
block|}
end_class

end_unit

