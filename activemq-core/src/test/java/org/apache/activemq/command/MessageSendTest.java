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
name|command
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|JmsQueueSendReceiveTwoConnectionsStartBeforeBrokerTest
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
name|ByteSequence
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

begin_class
specifier|public
class|class
name|MessageSendTest
extends|extends
name|DataStructureTestSupport
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
name|MessageSendTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|MessageSendTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestMessageSendMarshaling
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"cacheEnabled"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMessageSendMarshaling
parameter_list|()
throws|throws
name|IOException
block|{
name|ActiveMQMessage
name|message
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setCommandId
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"queue"
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setGroupID
argument_list|(
literal|"group"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setGroupSequence
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCorrelationId
argument_list|(
literal|"correlation"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
literal|"c1:1:1"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertBeanMarshalls
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|assertBeanMarshalls
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|xtestPerformance
parameter_list|()
throws|throws
name|IOException
block|{
name|ActiveMQMessage
name|message
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setProducerId
argument_list|(
operator|new
name|ProducerId
argument_list|(
operator|new
name|SessionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
name|message
operator|.
name|getProducerId
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCommandId
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|message
operator|.
name|setGroupID
argument_list|(
literal|"group"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setGroupSequence
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|message
operator|.
name|setCorrelationId
argument_list|(
literal|"correlation"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setContent
argument_list|(
operator|new
name|ByteSequence
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|p
init|=
literal|1000000
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|p
condition|;
name|i
operator|++
control|)
block|{
name|marshalAndUnmarshall
argument_list|(
name|message
argument_list|,
name|wireFormat
argument_list|)
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"marshaled/unmarshaled: "
operator|+
name|p
operator|+
literal|" msgs at "
operator|+
operator|(
name|p
operator|*
literal|1000f
operator|/
operator|(
name|end
operator|-
name|start
operator|)
operator|)
operator|+
literal|" msgs/sec"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

