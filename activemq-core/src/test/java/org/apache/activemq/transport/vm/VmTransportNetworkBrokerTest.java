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
name|vm
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|Connection
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
name|ActiveMQConnectionFactory
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
name|bugs
operator|.
name|embedded
operator|.
name|ThreadExplorer
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
name|network
operator|.
name|NetworkConnector
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|thread
operator|.
name|DefaultThreadPools
operator|.
name|shutdown
import|;
end_import

begin_class
specifier|public
class|class
name|VmTransportNetworkBrokerTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|VM_BROKER_URI
init|=
literal|"vm://localhost?create=false"
decl_stmt|;
name|CountDownLatch
name|started
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CountDownLatch
name|gotConnection
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testNoThreadLeakWithActiveVMConnection
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
name|setDedicatedTaskRunner
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616"
argument_list|)
expr_stmt|;
name|NetworkConnector
name|networkConnector
init|=
name|broker
operator|.
name|addNetworkConnector
argument_list|(
literal|"static:(tcp://wrongHostname1:61617,tcp://wrongHostname2:61618)?useExponentialBackOff=false"
argument_list|)
decl_stmt|;
name|networkConnector
operator|.
name|setDuplex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
operator|new
name|URI
argument_list|(
name|VM_BROKER_URI
argument_list|)
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// let it settle
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|int
name|threadCount
init|=
name|Thread
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|30
argument_list|)
expr_stmt|;
name|int
name|threadCountAfterSleep
init|=
name|Thread
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Threads are leaking: "
operator|+
name|ThreadExplorer
operator|.
name|show
argument_list|(
literal|"active sleep"
argument_list|)
operator|+
literal|", threadCount="
operator|+
name|threadCount
operator|+
literal|" threadCountAfterSleep="
operator|+
name|threadCountAfterSleep
argument_list|,
name|threadCountAfterSleep
operator|<
name|threadCount
operator|+
literal|8
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
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
specifier|public
name|void
name|testNoDanglingThreadsAfterStop
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|threadCount
init|=
name|Thread
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setSchedulerSupport
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDedicatedTaskRunner
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:61616?wireFormat.maxInactivityDuration=1000&wireFormat.maxInactivityDurationInitalDelay=1000"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"tcp://localhost:61616?wireFormat.maxInactivityDuration=1000&wireFormat.maxInactivityDurationInitalDelay=1000"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|shutdown
argument_list|()
expr_stmt|;
comment|// let it settle
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|int
name|threadCountAfterStop
init|=
name|Thread
operator|.
name|activeCount
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Threads are leaking: "
operator|+
name|ThreadExplorer
operator|.
name|show
argument_list|(
literal|"active after stop"
argument_list|)
operator|+
literal|". threadCount="
operator|+
name|threadCount
operator|+
literal|" threadCountAfterStop="
operator|+
name|threadCountAfterStop
argument_list|,
name|threadCountAfterStop
operator|==
name|threadCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

