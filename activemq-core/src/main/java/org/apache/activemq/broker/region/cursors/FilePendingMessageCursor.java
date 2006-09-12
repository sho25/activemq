begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|Iterator
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
name|Destination
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
name|MessageReference
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
name|Message
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
name|kaha
operator|.
name|ListContainer
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
name|kaha
operator|.
name|Store
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
name|openwire
operator|.
name|OpenWireFormat
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
name|kahadaptor
operator|.
name|CommandMarshaller
import|;
end_import

begin_comment
comment|/**  * perist pending messages pending message (messages awaiting disptach to a consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|FilePendingMessageCursor
extends|extends
name|AbstractPendingMessageCursor
block|{
specifier|private
name|ListContainer
name|list
decl_stmt|;
specifier|private
name|Iterator
name|iter
init|=
literal|null
decl_stmt|;
specifier|private
name|Destination
name|regionDestination
decl_stmt|;
comment|/**      * @param name      * @param store      * @throws IOException      */
specifier|public
name|FilePendingMessageCursor
parameter_list|(
name|String
name|name
parameter_list|,
name|Store
name|store
parameter_list|)
block|{
try|try
block|{
name|list
operator|=
name|store
operator|.
name|getListContainer
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|list
operator|.
name|setMarshaller
argument_list|(
operator|new
name|CommandMarshaller
argument_list|(
operator|new
name|OpenWireFormat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|setMaximumCacheSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return true if there are no pending messages      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|list
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * reset the cursor      *       */
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|iter
operator|=
name|list
operator|.
name|listIterator
argument_list|()
expr_stmt|;
block|}
comment|/**      * add message to await dispatch      *       * @param node      */
specifier|public
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
try|try
block|{
name|regionDestination
operator|=
name|node
operator|.
name|getMessage
argument_list|()
operator|.
name|getRegionDestination
argument_list|()
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|list
operator|.
name|addLast
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * add message to await dispatch      *       * @param position      * @param node      */
specifier|public
name|void
name|addMessageFirst
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
try|try
block|{
name|regionDestination
operator|=
name|node
operator|.
name|getMessage
argument_list|()
operator|.
name|getRegionDestination
argument_list|()
expr_stmt|;
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|list
operator|.
name|addFirst
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return true if there pending messages to dispatch      */
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
comment|/**      * @return the next pending message      */
specifier|public
name|MessageReference
name|next
parameter_list|()
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|message
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
return|return
name|message
return|;
block|}
comment|/**      * remove the message at the cursor position      *       */
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return the number of pending messages      */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * clear all pending messages      *       */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

