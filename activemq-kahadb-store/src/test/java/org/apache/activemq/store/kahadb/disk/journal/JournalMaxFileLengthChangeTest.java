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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
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
name|assertNotNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|ConnectionContext
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
name|ActiveMQTextMessage
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
name|MessageId
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
name|MessageStore
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
name|kahadb
operator|.
name|KahaDBStore
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
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
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
name|JournalMaxFileLengthChangeTest
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
name|JournalMaxFileLengthChangeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|int
name|ONE_MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|dataDir
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|Timeout
name|globalTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
specifier|private
name|KahaDBStore
name|store
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|store
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Test that reported size is correct if the maxFileLength grows      * in between journal restarts.  Verify all messages still received.      */
annotation|@
name|Test
specifier|public
name|void
name|testMaxFileLengthGrow
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageStore
name|messageStore
init|=
name|createStore
argument_list|(
literal|8
operator|*
name|ONE_MB
argument_list|)
decl_stmt|;
name|addMessages
argument_list|(
name|messageStore
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|long
name|sizeBeforeChange
init|=
name|store
operator|.
name|getJournal
argument_list|()
operator|.
name|getDiskSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Journal size before: "
operator|+
name|sizeBeforeChange
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
name|messageStore
operator|=
name|createStore
argument_list|(
literal|6
operator|*
name|ONE_MB
argument_list|)
expr_stmt|;
name|verifyMessages
argument_list|(
name|messageStore
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|long
name|sizeAfterChange
init|=
name|store
operator|.
name|getJournal
argument_list|()
operator|.
name|getDiskSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Journal size after: "
operator|+
name|sizeAfterChange
argument_list|)
expr_stmt|;
comment|//verify the size is the same - will be slightly different as checkpoint journal
comment|//commands are written but should be close
name|assertEquals
argument_list|(
name|sizeBeforeChange
argument_list|,
name|sizeAfterChange
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test that reported size is correct if the maxFileLength shrinks      * in between journal restarts.  Verify all messages still received.      */
annotation|@
name|Test
specifier|public
name|void
name|testMaxFileLengthShrink
parameter_list|()
throws|throws
name|Exception
block|{
name|MessageStore
name|messageStore
init|=
name|createStore
argument_list|(
literal|8
operator|*
name|ONE_MB
argument_list|)
decl_stmt|;
name|addMessages
argument_list|(
name|messageStore
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|long
name|sizeBeforeChange
init|=
name|store
operator|.
name|getJournal
argument_list|()
operator|.
name|getDiskSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Journal size before: "
operator|+
name|sizeBeforeChange
argument_list|)
expr_stmt|;
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
name|messageStore
operator|=
name|createStore
argument_list|(
literal|2
operator|*
name|ONE_MB
argument_list|)
expr_stmt|;
name|verifyMessages
argument_list|(
name|messageStore
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|long
name|sizeAfterChange
init|=
name|store
operator|.
name|getJournal
argument_list|()
operator|.
name|getDiskSize
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Journal size after: "
operator|+
name|sizeAfterChange
argument_list|)
expr_stmt|;
comment|//verify the size is the same - will be slightly different as checkpoint journal
comment|//commands are written but should be close
name|assertEquals
argument_list|(
name|sizeBeforeChange
argument_list|,
name|sizeAfterChange
argument_list|,
literal|4096
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addMessages
parameter_list|(
name|MessageStore
name|messageStore
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|text
init|=
name|getString
argument_list|(
name|ONE_MB
argument_list|)
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQTextMessage
name|textMessage
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|textMessage
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
literal|"1:2:3:"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|textMessage
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|messageStore
operator|.
name|addMessage
argument_list|(
operator|new
name|ConnectionContext
argument_list|()
argument_list|,
name|textMessage
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|verifyMessages
parameter_list|(
name|MessageStore
name|messageStore
parameter_list|,
name|int
name|num
parameter_list|)
throws|throws
name|Exception
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|assertNotNull
argument_list|(
name|messageStore
operator|.
name|getMessage
argument_list|(
operator|new
name|MessageId
argument_list|(
literal|"1:2:3:"
operator|+
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getString
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|MessageStore
name|createStore
parameter_list|(
name|int
name|length
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|dataDirectory
init|=
name|dataDir
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|store
operator|=
operator|new
name|KahaDBStore
argument_list|()
expr_stmt|;
name|store
operator|.
name|setJournalMaxFileLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|store
operator|.
name|setDirectory
argument_list|(
name|dataDirectory
argument_list|)
expr_stmt|;
name|store
operator|.
name|setForceRecoverIndex
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|store
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|store
operator|.
name|createQueueMessageStore
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"test"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

