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
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|// https://issues.apache.org/jira/browse/AMQ-3393
end_comment

begin_class
specifier|public
class|class
name|ConnectTest
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
name|ConnectTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|Vector
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|Vector
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|startBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
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
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"stomp://0.0.0.0:61612?transport.soLinger=0"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
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
annotation|@
name|Test
specifier|public
name|void
name|testStompConnectLeak
parameter_list|()
throws|throws
name|Exception
block|{
name|Thread
name|t1
init|=
operator|new
name|Thread
argument_list|()
block|{
name|StompConnection
name|connection
init|=
operator|new
name|StompConnection
argument_list|()
decl_stmt|;
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|connection
operator|.
name|open
argument_list|(
literal|"localhost"
argument_list|,
literal|61612
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|disconnect
argument_list|()
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
literal|"unexpected exception on connect/disconnect"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|long
name|done
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
operator|(
literal|60
operator|*
literal|1000
operator|*
literal|2
operator|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|done
condition|)
block|{
name|t1
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
operator|++
name|i
operator|%
literal|5000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"connection count on stomp connector:"
operator|+
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|connectionCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"no dangling connections"
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
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|connectionCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no exceptions"
argument_list|,
name|exceptions
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

