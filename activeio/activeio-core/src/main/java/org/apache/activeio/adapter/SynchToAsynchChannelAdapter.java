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
name|activeio
operator|.
name|adapter
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|Channel
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
name|packet
operator|.
name|async
operator|.
name|AsyncChannel
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
name|packet
operator|.
name|sync
operator|.
name|SyncChannel
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
name|concurrent
operator|.
name|Executor
import|;
end_import

begin_comment
comment|/**  * @deprecated  Use AsyncChannelServer instead.  This class will be removed very soon.  */
end_comment

begin_class
specifier|public
class|class
name|SynchToAsynchChannelAdapter
extends|extends
name|SyncToAsyncChannel
block|{
specifier|public
name|SynchToAsynchChannelAdapter
parameter_list|(
name|SyncChannel
name|syncChannel
parameter_list|)
block|{
name|super
argument_list|(
name|syncChannel
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SynchToAsynchChannelAdapter
parameter_list|(
name|SyncChannel
name|syncChannel
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
name|super
argument_list|(
name|syncChannel
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|public
name|AsyncChannel
name|adapt
parameter_list|(
name|Channel
name|channel
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
comment|// It might not need adapting
if|if
condition|(
name|channel
operator|instanceof
name|AsyncChannel
condition|)
block|{
return|return
operator|(
name|AsyncChannel
operator|)
name|channel
return|;
block|}
comment|// Can we just just undo the adaptor
if|if
condition|(
name|channel
operator|.
name|getClass
argument_list|()
operator|==
name|SyncToAsyncChannel
operator|.
name|class
condition|)
block|{
return|return
operator|(
operator|(
name|AsyncToSyncChannel
operator|)
name|channel
operator|)
operator|.
name|getAsyncChannel
argument_list|()
return|;
block|}
comment|// Can we just just undo the adaptor
if|if
condition|(
name|channel
operator|.
name|getClass
argument_list|()
operator|==
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|SynchToAsynchChannelAdapter
operator|.
name|class
condition|)
block|{
return|return
operator|(
operator|(
name|AsyncToSyncChannel
operator|)
name|channel
operator|)
operator|.
name|getAsyncChannel
argument_list|()
return|;
block|}
return|return
operator|new
name|SyncToAsyncChannel
argument_list|(
operator|(
name|SyncChannel
operator|)
name|channel
argument_list|,
name|executor
argument_list|)
return|;
block|}
block|}
end_class

end_unit

