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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|MessageReference
import|;
end_import

begin_interface
specifier|public
interface|interface
name|PendingList
extends|extends
name|Iterable
argument_list|<
name|MessageReference
argument_list|>
block|{
comment|/**      * Returns true if there are no Messages in the PendingList currently.      * @return true if the PendingList is currently empty.      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**      * Discards all Messages currently held in the PendingList.      */
specifier|public
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**      * Adds the given message to the head of the list.      *      * @param message      *      The MessageReference that is to be added to this list.      *      * @return the PendingNode that contains the newly added message.      */
specifier|public
name|PendingNode
name|addMessageFirst
parameter_list|(
name|MessageReference
name|message
parameter_list|)
function_decl|;
comment|/**      * Adds the given message to the tail of the list.      *      * @param message      *      The MessageReference that is to be added to this list.      *      * @return the PendingNode that contains the newly added message.      */
specifier|public
name|PendingNode
name|addMessageLast
parameter_list|(
name|MessageReference
name|message
parameter_list|)
function_decl|;
comment|/**      * Removes the given MessageReference from the PendingList if it is      * contained within.      *      * @param message      *      The MessageReference that is to be removed to this list.      *      * @return the PendingNode that contains the removed message or null if the      *         message was not present in this list.      */
specifier|public
name|PendingNode
name|remove
parameter_list|(
name|MessageReference
name|message
parameter_list|)
function_decl|;
comment|/**      * Returns the number of MessageReferences that are awaiting dispatch.      * @return current count of the pending messages.      */
specifier|public
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * Returns an iterator over the pending Messages.  The subclass controls how      * the returned iterator actually traverses the list of pending messages allowing      * for the order to vary based on factors like Message priority or some other      * mechanism.      *      * @return an Iterator that returns MessageReferences contained in this list.      */
specifier|public
name|Iterator
argument_list|<
name|MessageReference
argument_list|>
name|iterator
parameter_list|()
function_decl|;
comment|/**      * Query the PendingList to determine if the given message is contained within.      *      * @param message      *      The Message that is the target of this query.      *      * @return true if the MessageReference is contained in this list.      */
specifier|public
name|boolean
name|contains
parameter_list|(
name|MessageReference
name|message
parameter_list|)
function_decl|;
comment|/**      * Returns a new Collection that contains all the MessageReferences currently      * held in this PendingList.  The elements of the list are ordered using the      * same rules as the subclass uses for iteration.      *      * @return a new Collection containing this lists MessageReferences.      */
specifier|public
name|Collection
argument_list|<
name|MessageReference
argument_list|>
name|values
parameter_list|()
function_decl|;
comment|/**      * Adds all the elements of the given PendingList to this PendingList.      *      * @param pendingList      *      The PendingList that is to be added to this collection.      */
specifier|public
name|void
name|addAll
parameter_list|(
name|PendingList
name|pendingList
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

