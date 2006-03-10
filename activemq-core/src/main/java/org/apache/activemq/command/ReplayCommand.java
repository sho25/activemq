begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
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
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  * A general purpose replay command for some kind of producer where ranges of  * messages are asked to be replayed. This command is typically used over a  * non-reliable transport such as UDP or multicast but could also be used on  * TCP/IP if a socket has been re-established.  *   * @openwire:marshaller code="65"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ReplayCommand
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|REPLAY
decl_stmt|;
specifier|private
name|String
name|producerId
decl_stmt|;
specifier|private
name|int
name|firstAckNumber
decl_stmt|;
specifier|private
name|int
name|lastAckNumber
decl_stmt|;
specifier|private
name|int
name|firstNakNumber
decl_stmt|;
specifier|private
name|int
name|lastNakNumber
decl_stmt|;
specifier|public
name|ReplayCommand
parameter_list|()
block|{     }
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
specifier|public
name|String
name|getProducerId
parameter_list|()
block|{
return|return
name|producerId
return|;
block|}
comment|/**      * Is used to uniquely identify the producer of the sequence      *       * @openwire:property version=1 cache=false      */
specifier|public
name|void
name|setProducerId
parameter_list|(
name|String
name|producerId
parameter_list|)
block|{
name|this
operator|.
name|producerId
operator|=
name|producerId
expr_stmt|;
block|}
specifier|public
name|int
name|getFirstAckNumber
parameter_list|()
block|{
return|return
name|firstAckNumber
return|;
block|}
comment|/**      * Is used to specify the first sequence number being acknowledged as delivered on the transport      * so that it can be removed from cache      *       * @openwire:property version=1      */
specifier|public
name|void
name|setFirstAckNumber
parameter_list|(
name|int
name|firstSequenceNumber
parameter_list|)
block|{
name|this
operator|.
name|firstAckNumber
operator|=
name|firstSequenceNumber
expr_stmt|;
block|}
specifier|public
name|int
name|getLastAckNumber
parameter_list|()
block|{
return|return
name|lastAckNumber
return|;
block|}
comment|/**      * Is used to specify the last sequence number being acknowledged as delivered on the transport      * so that it can be removed from cache      *       * @openwire:property version=1      */
specifier|public
name|void
name|setLastAckNumber
parameter_list|(
name|int
name|lastSequenceNumber
parameter_list|)
block|{
name|this
operator|.
name|lastAckNumber
operator|=
name|lastSequenceNumber
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Is used to specify the first sequence number to be replayed      *       * @openwire:property version=1      */
specifier|public
name|int
name|getFirstNakNumber
parameter_list|()
block|{
return|return
name|firstNakNumber
return|;
block|}
specifier|public
name|void
name|setFirstNakNumber
parameter_list|(
name|int
name|firstNakNumber
parameter_list|)
block|{
name|this
operator|.
name|firstNakNumber
operator|=
name|firstNakNumber
expr_stmt|;
block|}
comment|/**      * Is used to specify the last sequence number to be replayed      *       * @openwire:property version=1      */
specifier|public
name|int
name|getLastNakNumber
parameter_list|()
block|{
return|return
name|lastNakNumber
return|;
block|}
specifier|public
name|void
name|setLastNakNumber
parameter_list|(
name|int
name|lastNakNumber
parameter_list|)
block|{
name|this
operator|.
name|lastNakNumber
operator|=
name|lastNakNumber
expr_stmt|;
block|}
block|}
end_class

end_unit

