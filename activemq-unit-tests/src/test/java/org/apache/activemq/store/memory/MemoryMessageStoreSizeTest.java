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
name|memory
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
name|store
operator|.
name|AbstractMessageStoreSizeTest
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

begin_class
specifier|public
class|class
name|MemoryMessageStoreSizeTest
extends|extends
name|AbstractMessageStoreSizeTest
block|{
name|MemoryMessageStore
name|messageStore
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initStore
parameter_list|()
throws|throws
name|Exception
block|{
name|messageStore
operator|=
operator|new
name|MemoryMessageStore
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|messageStore
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|destroyStore
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|messageStore
operator|!=
literal|null
condition|)
block|{
name|messageStore
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|MessageStore
name|getMessageStore
parameter_list|()
block|{
return|return
name|messageStore
return|;
block|}
block|}
end_class

end_unit

