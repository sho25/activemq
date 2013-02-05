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
name|memory
operator|.
name|buffer
package|;
end_package

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
name|command
operator|.
name|ActiveMQMessage
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
name|memory
operator|.
name|buffer
operator|.
name|MessageBuffer
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
name|memory
operator|.
name|buffer
operator|.
name|MessageQueue
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
comment|/**  *  *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MemoryBufferTestSupport
extends|extends
name|TestCase
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
name|MemoryBufferTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|MessageBuffer
name|buffer
init|=
name|createMessageBuffer
argument_list|()
decl_stmt|;
specifier|protected
name|MessageQueue
name|qA
init|=
name|buffer
operator|.
name|createMessageQueue
argument_list|()
decl_stmt|;
specifier|protected
name|MessageQueue
name|qB
init|=
name|buffer
operator|.
name|createMessageQueue
argument_list|()
decl_stmt|;
specifier|protected
name|MessageQueue
name|qC
init|=
name|buffer
operator|.
name|createMessageQueue
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|messageCount
decl_stmt|;
specifier|protected
specifier|abstract
name|MessageBuffer
name|createMessageBuffer
parameter_list|()
function_decl|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|buffer
operator|=
name|createMessageBuffer
argument_list|()
expr_stmt|;
name|qA
operator|=
name|buffer
operator|.
name|createMessageQueue
argument_list|()
expr_stmt|;
name|qB
operator|=
name|buffer
operator|.
name|createMessageQueue
argument_list|()
expr_stmt|;
name|qC
operator|=
name|buffer
operator|.
name|createMessageQueue
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|dump
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Dumping current state"
argument_list|)
expr_stmt|;
name|dumpQueue
argument_list|(
name|qA
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|dumpQueue
argument_list|(
name|qB
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|dumpQueue
argument_list|(
name|qC
argument_list|,
literal|"C"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|dumpQueue
parameter_list|(
name|MessageQueue
name|queue
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"  "
operator|+
name|name
operator|+
literal|" = "
operator|+
name|queue
operator|.
name|getList
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQMessage
name|createMessage
parameter_list|(
name|int
name|size
parameter_list|)
throws|throws
name|Exception
block|{
name|DummyMessage
name|answer
init|=
operator|new
name|DummyMessage
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|answer
operator|.
name|setIntProperty
argument_list|(
literal|"counter"
argument_list|,
operator|++
name|messageCount
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setJMSMessageID
argument_list|(
literal|""
operator|+
name|messageCount
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit
