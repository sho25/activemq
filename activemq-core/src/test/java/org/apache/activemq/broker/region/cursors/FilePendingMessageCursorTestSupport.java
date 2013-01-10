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
name|region
operator|.
name|cursors
package|;
end_package

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
name|broker
operator|.
name|region
operator|.
name|QueueMessageReference
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
name|PList
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
name|usage
operator|.
name|SystemUsage
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|FilePendingMessageCursorTestSupport
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FilePendingMessageCursorTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|brokerService
decl_stmt|;
specifier|protected
name|FilePendingMessageCursor
name|underTest
decl_stmt|;
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
name|getTempDataStore
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createBrokerWithTempStoreLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|SystemUsage
name|usage
init|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
decl_stmt|;
name|usage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1025
operator|*
literal|1024
operator|*
literal|15
argument_list|)
expr_stmt|;
comment|// put something in the temp store to on demand initialise it
name|PList
name|dud
init|=
name|brokerService
operator|.
name|getTempDataStore
argument_list|()
operator|.
name|getPList
argument_list|(
literal|"dud"
argument_list|)
decl_stmt|;
name|dud
operator|.
name|addFirst
argument_list|(
literal|"A"
argument_list|,
operator|new
name|ByteSequence
argument_list|(
literal|"A"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddToEmptyCursorWhenTempStoreIsFull
parameter_list|()
throws|throws
name|Exception
block|{
name|createBrokerWithTempStoreLimit
argument_list|()
expr_stmt|;
name|SystemUsage
name|usage
init|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"temp store is full: %"
operator|+
name|usage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
argument_list|,
name|usage
operator|.
name|getTempUsage
argument_list|()
operator|.
name|isFull
argument_list|()
argument_list|)
expr_stmt|;
name|underTest
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|brokerService
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|setSystemUsage
argument_list|(
name|usage
argument_list|)
expr_stmt|;
comment|// ok to add
name|underTest
operator|.
name|addMessageLast
argument_list|(
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"cursor is not full"
argument_list|,
name|underTest
operator|.
name|isFull
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testResetClearsIterator
parameter_list|()
throws|throws
name|Exception
block|{
name|createBrokerWithTempStoreLimit
argument_list|()
expr_stmt|;
name|underTest
operator|=
operator|new
name|FilePendingMessageCursor
argument_list|(
name|brokerService
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// ok to add
name|underTest
operator|.
name|addMessageLast
argument_list|(
name|QueueMessageReference
operator|.
name|NULL_MESSAGE
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|reset
argument_list|()
expr_stmt|;
name|underTest
operator|.
name|release
argument_list|()
expr_stmt|;
try|try
block|{
name|underTest
operator|.
name|hasNext
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expect npe on use of iterator after release"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|expected
parameter_list|)
block|{}
block|}
block|}
end_class

end_unit

