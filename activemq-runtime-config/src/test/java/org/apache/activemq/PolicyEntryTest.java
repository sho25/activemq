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
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
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
name|junit
operator|.
name|Test
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

begin_class
specifier|public
class|class
name|PolicyEntryTest
extends|extends
name|RuntimeConfigTestSupport
block|{
name|String
name|configurationSeed
init|=
literal|"policyEntryTest"
decl_stmt|;
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
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-policy-ml-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
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
name|verifyQueueLimit
argument_list|(
literal|"Before"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml-mod"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|verifyQueueLimit
argument_list|(
literal|"After"
argument_list|,
literal|4194304
argument_list|)
expr_stmt|;
comment|// change to existing dest
name|verifyQueueLimit
argument_list|(
literal|"Before"
argument_list|,
literal|4194304
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddNdMod
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-policy-ml-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
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
name|verifyQueueLimit
argument_list|(
literal|"Before"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|verifyTopicLimit
argument_list|(
literal|"Before"
argument_list|,
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml-add"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|verifyTopicLimit
argument_list|(
literal|"After"
argument_list|,
literal|2048l
argument_list|)
expr_stmt|;
name|verifyQueueLimit
argument_list|(
literal|"After"
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
comment|// change to existing dest
name|verifyTopicLimit
argument_list|(
literal|"Before"
argument_list|,
literal|2048l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModParentPolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-policy-ml-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml-parent"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
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
name|verifyQueueLimit
argument_list|(
literal|"queue.test"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|verifyQueueLimit
argument_list|(
literal|"queue.child.test"
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml-parent-mod"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
name|verifyQueueLimit
argument_list|(
literal|"queue.test2"
argument_list|,
literal|4194304
argument_list|)
expr_stmt|;
comment|// change to existing dest
name|verifyQueueLimit
argument_list|(
literal|"queue.test"
argument_list|,
literal|4194304
argument_list|)
expr_stmt|;
comment|//verify no change
name|verifyQueueLimit
argument_list|(
literal|"queue.child.test"
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testModChildPolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|brokerConfig
init|=
name|configurationSeed
operator|+
literal|"-policy-ml-broker"
decl_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml-parent"
argument_list|)
expr_stmt|;
name|startBroker
argument_list|(
name|brokerConfig
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
name|verifyQueueLimit
argument_list|(
literal|"queue.test"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
name|verifyQueueLimit
argument_list|(
literal|"queue.child.test"
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
name|applyNewConfig
argument_list|(
name|brokerConfig
argument_list|,
name|configurationSeed
operator|+
literal|"-policy-ml-child-mod"
argument_list|,
name|SLEEP
argument_list|)
expr_stmt|;
comment|//verify no change
name|verifyQueueLimit
argument_list|(
literal|"queue.test"
argument_list|,
literal|1024
argument_list|)
expr_stmt|;
comment|// change to existing dest
name|verifyQueueLimit
argument_list|(
literal|"queue.child.test"
argument_list|,
literal|4194304
argument_list|)
expr_stmt|;
comment|//new dest change
name|verifyQueueLimit
argument_list|(
literal|"queue.child.test2"
argument_list|,
literal|4194304
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyQueueLimit
parameter_list|(
name|String
name|dest
parameter_list|,
name|int
name|memoryLimit
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
operator|.
name|createActiveMQConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
name|dest
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|memoryLimit
argument_list|,
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
operator|new
name|ActiveMQQueue
argument_list|(
name|dest
argument_list|)
argument_list|)
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|verifyTopicLimit
parameter_list|(
name|String
name|dest
parameter_list|,
name|long
name|memoryLimit
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnection
name|connection
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
operator|.
name|createActiveMQConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|session
operator|.
name|createConsumer
argument_list|(
name|session
operator|.
name|createTopic
argument_list|(
name|dest
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|memoryLimit
argument_list|,
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
operator|new
name|ActiveMQTopic
argument_list|(
name|dest
argument_list|)
argument_list|)
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

