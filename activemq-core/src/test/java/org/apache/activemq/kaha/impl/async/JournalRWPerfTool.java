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
name|kaha
operator|.
name|impl
operator|.
name|async
package|;
end_package

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|Journal
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|journal
operator|.
name|JournalRWPerfToolSupport
import|;
end_import

begin_comment
comment|/**  * A Performance statistics gathering tool for the AsyncDataManager based  * Journal.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|JournalRWPerfTool
extends|extends
name|JournalRWPerfToolSupport
block|{
specifier|private
name|int
name|logFileSize
init|=
literal|1024
operator|*
literal|1024
operator|*
literal|50
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|JournalRWPerfTool
name|tool
init|=
operator|new
name|JournalRWPerfTool
argument_list|()
decl_stmt|;
name|tool
operator|.
name|initialWriteWorkers
operator|=
literal|10
expr_stmt|;
name|tool
operator|.
name|syncFrequency
operator|=
literal|15
expr_stmt|;
name|tool
operator|.
name|writeWorkerIncrement
operator|=
literal|0
expr_stmt|;
name|tool
operator|.
name|writeWorkerThinkTime
operator|=
literal|0
expr_stmt|;
name|tool
operator|.
name|verbose
operator|=
literal|false
expr_stmt|;
name|tool
operator|.
name|incrementDelay
operator|=
literal|5
operator|*
literal|1000
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|tool
operator|.
name|journalDirectory
operator|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|tool
operator|.
name|writeWorkerIncrement
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
condition|)
block|{
name|tool
operator|.
name|incrementDelay
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|3
condition|)
block|{
name|tool
operator|.
name|verbose
operator|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|4
condition|)
block|{
name|tool
operator|.
name|recordSize
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|5
condition|)
block|{
name|tool
operator|.
name|syncFrequency
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|6
condition|)
block|{
name|tool
operator|.
name|writeWorkerThinkTime
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|6
index|]
argument_list|)
expr_stmt|;
block|}
name|tool
operator|.
name|exec
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws IOException      * @see org.apache.activeio.journal.JournalPerfToolSupport#createJournal()      */
specifier|public
name|Journal
name|createJournal
parameter_list|()
throws|throws
name|IOException
block|{
name|AsyncDataManager
name|dm
init|=
operator|new
name|AsyncDataManager
argument_list|()
decl_stmt|;
name|dm
operator|.
name|setMaxFileLength
argument_list|(
name|logFileSize
argument_list|)
expr_stmt|;
name|dm
operator|.
name|setDirectory
argument_list|(
name|this
operator|.
name|journalDirectory
argument_list|)
expr_stmt|;
name|dm
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
operator|new
name|JournalFacade
argument_list|(
name|dm
argument_list|)
return|;
block|}
block|}
end_class

end_unit

