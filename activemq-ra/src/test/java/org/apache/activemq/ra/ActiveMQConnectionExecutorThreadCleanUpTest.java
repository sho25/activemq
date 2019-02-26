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
name|ra
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
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
name|ra
operator|.
name|ActiveMQResourceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * Test for AMQ-6700.  * Will fail to connect to embedded broker using JCA and uses  * "ActiveMQ Connection Executor" thread to deal with low  * level exception. This tests verifies if this thread gets  * cleared up correctly after use.  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQConnectionExecutorThreadCleanUpTest
block|{
specifier|protected
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ActiveMQConnectionExecutorThreadCleanUpTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|AMQ_CONN_EXECUTOR_THREAD_NAME
init|=
literal|"ActiveMQ Connection Executor"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Configuring broker programmatically."
argument_list|)
expr_stmt|;
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// explicitly limiting to 0 connections so that test is unable
comment|// to connect
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0?maximumConnections=0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|shutDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|broker
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * This test tries to create connections into the broker using the      * resource adapter's transaction recovery functionality.      * If the broker does not accept the connection, the connection's      * thread pool executor is used to deal with the error.      * This has lead to race conditions where the thread was not shutdown      * but got leaked.      * @throws Exception      */
annotation|@
name|Test
specifier|public
name|void
name|testAMQConnectionExecutorThreadCleanUp
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"testAMQConnectionExecutorThreadCleanUp() started."
argument_list|)
expr_stmt|;
name|ActiveMQResourceAdapter
name|ra
init|=
operator|new
name|ActiveMQResourceAdapter
argument_list|()
decl_stmt|;
name|ra
operator|.
name|setServerUrl
argument_list|(
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using brokerUrl "
operator|+
name|ra
operator|.
name|getServerUrl
argument_list|()
argument_list|)
expr_stmt|;
comment|// running in a small loop as very occasionally the call to
comment|// ActiveMQResourceAdapter.$2.makeConnection() raises an exception
comment|// rather than using the connection's executor task to deal with the
comment|// connection error.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Iteration "
operator|+
name|i
argument_list|)
expr_stmt|;
name|ra
operator|.
name|start
argument_list|(
literal|null
argument_list|)
expr_stmt|;
try|try
block|{
name|XAResource
index|[]
name|resources
init|=
name|ra
operator|.
name|getXAResources
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|resources
index|[
literal|0
index|]
operator|.
name|recover
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ra
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// allow some small time for thread cleanup to happen
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
comment|// check if thread exists
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"Thread named \""
operator|+
name|AMQ_CONN_EXECUTOR_THREAD_NAME
operator|+
literal|"\" not cleared up with ActiveMQConnection."
argument_list|,
name|hasActiveMQConnectionExceutorThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Retrieves all threads from JVM and checks if any thread names contain      * AMQ_CONN_EXECUTOR_THREAD_NAME.      *      * @return true if such thread exists, otherwise false      */
specifier|public
name|boolean
name|hasActiveMQConnectionExceutorThread
parameter_list|()
block|{
comment|// retrieve all threads
name|Set
argument_list|<
name|Thread
argument_list|>
name|threadSet
init|=
name|Thread
operator|.
name|getAllStackTraces
argument_list|()
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Thread
argument_list|>
name|iter
init|=
name|threadSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Thread
name|thread
init|=
operator|(
name|Thread
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|thread
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|AMQ_CONN_EXECUTOR_THREAD_NAME
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Thread with name {} found."
argument_list|,
name|thread
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Thread with name {} not found."
argument_list|,
name|AMQ_CONN_EXECUTOR_THREAD_NAME
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

