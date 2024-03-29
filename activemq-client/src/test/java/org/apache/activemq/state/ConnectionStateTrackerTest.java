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
name|state
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|ConsumerId
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
name|MessagePull
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
name|SessionId
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

begin_class
specifier|public
class|class
name|ConnectionStateTrackerTest
block|{
specifier|private
specifier|final
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Test"
argument_list|)
decl_stmt|;
specifier|private
name|ConnectionId
name|testConnectionId
decl_stmt|;
specifier|private
name|SessionId
name|testSessionId
decl_stmt|;
specifier|private
name|int
name|connectionId
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|sessionId
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|consumerId
init|=
literal|0
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
name|testConnectionId
operator|=
name|createConnectionId
argument_list|()
expr_stmt|;
name|testSessionId
operator|=
name|createSessionId
argument_list|(
name|testConnectionId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Test
specifier|public
name|void
name|testCacheSizeWithMessagePulls
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ConsumerId
name|consumer1
init|=
name|createConsumerId
argument_list|(
name|testSessionId
argument_list|)
decl_stmt|;
name|ConnectionStateTracker
name|tracker
init|=
operator|new
name|ConnectionStateTracker
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tracker
operator|.
name|getCurrentCacheSize
argument_list|()
argument_list|)
expr_stmt|;
name|MessagePull
name|pullCommand
init|=
name|createPullCommand
argument_list|(
name|consumer1
argument_list|)
decl_stmt|;
name|tracker
operator|.
name|track
argument_list|(
name|pullCommand
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tracker
operator|.
name|getCurrentCacheSize
argument_list|()
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|trackBack
argument_list|(
name|pullCommand
argument_list|)
expr_stmt|;
name|long
name|currentSize
init|=
name|tracker
operator|.
name|getCurrentCacheSize
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|currentSize
operator|>
literal|0
argument_list|)
expr_stmt|;
name|pullCommand
operator|=
name|createPullCommand
argument_list|(
name|consumer1
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|track
argument_list|(
name|pullCommand
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|trackBack
argument_list|(
name|pullCommand
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|currentSize
argument_list|,
name|tracker
operator|.
name|getCurrentCacheSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|MessagePull
name|createPullCommand
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
block|{
name|MessagePull
name|pullCommand
init|=
operator|new
name|MessagePull
argument_list|()
decl_stmt|;
name|pullCommand
operator|.
name|setDestination
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|pullCommand
operator|.
name|setConsumerId
argument_list|(
name|id
argument_list|)
expr_stmt|;
return|return
name|pullCommand
return|;
block|}
specifier|private
name|ConnectionId
name|createConnectionId
parameter_list|()
block|{
name|ConnectionId
name|id
init|=
operator|new
name|ConnectionId
argument_list|()
decl_stmt|;
name|id
operator|.
name|setValue
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|+
literal|":"
operator|+
name|connectionId
operator|++
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
specifier|private
name|SessionId
name|createSessionId
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
block|{
return|return
operator|new
name|SessionId
argument_list|(
name|connectionId
argument_list|,
name|sessionId
operator|++
argument_list|)
return|;
block|}
specifier|private
name|ConsumerId
name|createConsumerId
parameter_list|(
name|SessionId
name|sessionId
parameter_list|)
block|{
return|return
operator|new
name|ConsumerId
argument_list|(
name|sessionId
argument_list|,
name|consumerId
operator|++
argument_list|)
return|;
block|}
block|}
end_class

end_unit

