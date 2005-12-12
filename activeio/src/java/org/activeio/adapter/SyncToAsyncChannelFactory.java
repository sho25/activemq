begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|adapter
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|AsyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|AsyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|AsyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|ChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|SyncChannelFactory
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
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SyncToAsyncChannelFactory
implements|implements
name|AsyncChannelFactory
block|{
specifier|private
specifier|final
name|SyncChannelFactory
name|syncChannelFactory
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
specifier|static
specifier|public
name|AsyncChannelFactory
name|adapt
parameter_list|(
name|SyncChannelFactory
name|channelFactory
parameter_list|)
block|{
return|return
name|adapt
argument_list|(
name|channelFactory
argument_list|,
name|ChannelFactory
operator|.
name|DEFAULT_EXECUTOR
argument_list|)
return|;
block|}
specifier|static
specifier|public
name|AsyncChannelFactory
name|adapt
parameter_list|(
name|SyncChannelFactory
name|channelFactory
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
comment|// It might not need adapting
if|if
condition|(
name|channelFactory
operator|instanceof
name|AsyncChannelFactory
condition|)
block|{
return|return
operator|(
name|AsyncChannelFactory
operator|)
name|channelFactory
return|;
block|}
comment|// Can we just just undo the adaptor
if|if
condition|(
name|channelFactory
operator|.
name|getClass
argument_list|()
operator|==
name|AsyncToSyncChannelFactory
operator|.
name|class
condition|)
block|{
return|return
operator|(
operator|(
name|AsyncToSyncChannelFactory
operator|)
name|channelFactory
operator|)
operator|.
name|getAsyncChannelFactory
argument_list|()
return|;
block|}
return|return
operator|new
name|SyncToAsyncChannelFactory
argument_list|(
operator|(
name|SyncChannelFactory
operator|)
name|channelFactory
argument_list|,
name|executor
argument_list|)
return|;
block|}
comment|/**      * @deprecated {@see #adapt(SyncChannelFactory)}      */
specifier|public
name|SyncToAsyncChannelFactory
parameter_list|(
specifier|final
name|SyncChannelFactory
name|next
parameter_list|)
block|{
name|this
argument_list|(
name|next
argument_list|,
name|ChannelFactory
operator|.
name|DEFAULT_EXECUTOR
argument_list|)
expr_stmt|;
block|}
comment|/**      * @deprecated {@see #adapt(SyncChannelFactory, Executor)}      */
specifier|public
name|SyncToAsyncChannelFactory
parameter_list|(
specifier|final
name|SyncChannelFactory
name|next
parameter_list|,
name|Executor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|syncChannelFactory
operator|=
name|next
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
specifier|public
name|AsyncChannel
name|openAsyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|SyncToAsyncChannel
operator|.
name|adapt
argument_list|(
name|syncChannelFactory
operator|.
name|openSyncChannel
argument_list|(
name|location
argument_list|)
argument_list|,
name|executor
argument_list|)
return|;
block|}
specifier|public
name|AsyncChannelServer
name|bindAsyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SyncToAsyncChannelServer
argument_list|(
name|syncChannelFactory
operator|.
name|bindSyncChannel
argument_list|(
name|location
argument_list|)
argument_list|,
name|executor
argument_list|)
return|;
block|}
specifier|public
name|SyncChannelFactory
name|getSyncChannelFactory
parameter_list|()
block|{
return|return
name|syncChannelFactory
return|;
block|}
block|}
end_class

end_unit

