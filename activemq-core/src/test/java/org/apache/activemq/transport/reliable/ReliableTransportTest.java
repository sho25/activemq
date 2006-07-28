begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|reliable
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
name|ConsumerInfo
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
name|transport
operator|.
name|StubTransport
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
name|transport
operator|.
name|StubTransportListener
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

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ReliableTransportTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ReliableTransportTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ReliableTransport
name|transport
decl_stmt|;
specifier|protected
name|StubTransportListener
name|listener
init|=
operator|new
name|StubTransportListener
argument_list|()
decl_stmt|;
specifier|protected
name|ReplayStrategy
name|replayStrategy
decl_stmt|;
specifier|public
name|void
name|testValidSequenceOfPackets
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|sequenceNumbers
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|}
decl_stmt|;
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testValidWrapAroundPackets
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|sequenceNumbers
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
name|int
name|value
init|=
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|3
decl_stmt|;
name|transport
operator|.
name|setExpectedCounter
argument_list|(
name|value
argument_list|)
expr_stmt|;
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
name|log
operator|.
name|info
argument_list|(
literal|"command: "
operator|+
name|i
operator|+
literal|" = "
operator|+
name|value
argument_list|)
expr_stmt|;
name|sequenceNumbers
index|[
name|i
index|]
operator|=
name|value
operator|++
expr_stmt|;
block|}
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDuplicatePacketsDropped
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|sequenceNumbers
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|}
decl_stmt|;
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
literal|true
argument_list|,
literal|7
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOldDuplicatePacketsDropped
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|sequenceNumbers
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|2
block|,
literal|6
block|,
literal|7
block|}
decl_stmt|;
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
literal|true
argument_list|,
literal|7
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOldDuplicatePacketsDroppedUsingNegativeCounters
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|sequenceNumbers
init|=
block|{
operator|-
literal|3
block|,
operator|-
literal|1
block|,
operator|-
literal|3
block|,
operator|-
literal|2
block|,
operator|-
literal|1
block|,
literal|0
block|,
literal|1
block|,
operator|-
literal|1
block|,
literal|3
block|,
literal|2
block|,
literal|0
block|,
literal|2
block|,
literal|4
block|}
decl_stmt|;
name|transport
operator|.
name|setExpectedCounter
argument_list|(
operator|-
literal|3
argument_list|)
expr_stmt|;
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
literal|true
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWrongOrderOfPackets
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|sequenceNumbers
init|=
block|{
literal|4
block|,
literal|3
block|,
literal|1
block|,
literal|5
block|,
literal|2
block|,
literal|7
block|,
literal|6
block|,
literal|8
block|,
literal|10
block|,
literal|9
block|}
decl_stmt|;
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMissingPacketsFails
parameter_list|()
throws|throws
name|Exception
block|{
name|int
index|[]
name|sequenceNumbers
init|=
block|{
literal|1
block|,
literal|2
block|,
comment|/* 3, */
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|7
block|,
literal|8
block|,
literal|9
block|,
literal|10
block|}
decl_stmt|;
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendStreamOfCommands
parameter_list|(
name|int
index|[]
name|sequenceNumbers
parameter_list|,
name|boolean
name|expected
parameter_list|)
block|{
name|sendStreamOfCommands
argument_list|(
name|sequenceNumbers
argument_list|,
name|expected
argument_list|,
name|sequenceNumbers
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendStreamOfCommands
parameter_list|(
name|int
index|[]
name|sequenceNumbers
parameter_list|,
name|boolean
name|expected
parameter_list|,
name|int
name|expectedCount
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sequenceNumbers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|commandId
init|=
name|sequenceNumbers
index|[
name|i
index|]
decl_stmt|;
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setSelector
argument_list|(
literal|"Cheese: "
operator|+
name|commandId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setCommandId
argument_list|(
name|commandId
argument_list|)
expr_stmt|;
name|transport
operator|.
name|onCommand
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|Queue
name|exceptions
init|=
name|listener
operator|.
name|getExceptions
argument_list|()
decl_stmt|;
name|Queue
name|commands
init|=
name|listener
operator|.
name|getCommands
argument_list|()
decl_stmt|;
if|if
condition|(
name|expected
condition|)
block|{
if|if
condition|(
operator|!
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Exception
name|e
init|=
operator|(
name|Exception
operator|)
name|exceptions
operator|.
name|remove
argument_list|()
decl_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Caught exception: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"number of messages received"
argument_list|,
name|expectedCount
argument_list|,
name|commands
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should have no buffered commands"
argument_list|,
literal|0
argument_list|,
name|transport
operator|.
name|getBufferedCommandCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
literal|"Should have received an exception!"
argument_list|,
name|exceptions
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Exception
name|e
init|=
operator|(
name|Exception
operator|)
name|exceptions
operator|.
name|remove
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Caught expected response: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|replayStrategy
operator|==
literal|null
condition|)
block|{
name|replayStrategy
operator|=
operator|new
name|ExceptionIfDroppedReplayStrategy
argument_list|()
expr_stmt|;
block|}
name|transport
operator|=
operator|new
name|ReliableTransport
argument_list|(
operator|new
name|StubTransport
argument_list|()
argument_list|,
name|replayStrategy
argument_list|)
expr_stmt|;
name|transport
operator|.
name|setTransportListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

