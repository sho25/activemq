begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Externalizable
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
name|java
operator|.
name|io
operator|.
name|ObjectInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|DestinationMarshaller
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activecluster
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Default implementation of a remote Node  *   * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|NodeState
implements|implements
name|Externalizable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3909792803360045064L
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|destinationName
decl_stmt|;
specifier|protected
name|Map
name|state
decl_stmt|;
specifier|protected
name|boolean
name|coordinator
decl_stmt|;
comment|/**      * DefaultConstructor      *      */
specifier|public
name|NodeState
parameter_list|()
block|{     }
comment|/**      * Construct a NodeState from a Node      * @param node      * @param marshaller      */
specifier|public
name|NodeState
parameter_list|(
name|Node
name|node
parameter_list|,
name|DestinationMarshaller
name|marshaller
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|node
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|destinationName
operator|=
name|marshaller
operator|.
name|getDestinationName
argument_list|(
name|node
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|node
operator|.
name|getState
argument_list|()
expr_stmt|;
name|this
operator|.
name|coordinator
operator|=
name|node
operator|.
name|isCoordinator
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return pretty print of the node      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NodeState[<"
operator|+
name|name
operator|+
literal|">destinationName: "
operator|+
name|destinationName
operator|+
literal|" state: "
operator|+
name|state
operator|+
literal|"]"
return|;
block|}
comment|/**      * @return Returns the coordinator.      */
specifier|public
name|boolean
name|isCoordinator
parameter_list|()
block|{
return|return
name|coordinator
return|;
block|}
comment|/**      * @param coordinator      *            The coordinator to set.      */
specifier|public
name|void
name|setCoordinator
parameter_list|(
name|boolean
name|coordinator
parameter_list|)
block|{
name|this
operator|.
name|coordinator
operator|=
name|coordinator
expr_stmt|;
block|}
comment|/**      * @return Returns the destinationName.      */
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
name|destinationName
return|;
block|}
comment|/**      * @param destinationName      *            The destinationName to set.      */
specifier|public
name|void
name|setDestinationName
parameter_list|(
name|String
name|destinationName
parameter_list|)
block|{
name|this
operator|.
name|destinationName
operator|=
name|destinationName
expr_stmt|;
block|}
comment|/**      * @return Returns the name.      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * @param name      *            The name to set.      */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * @return Returns the state.      */
specifier|public
name|Map
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**      * @param state      *            The state to set.      */
specifier|public
name|void
name|setState
parameter_list|(
name|Map
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
comment|/**      * write to a stream      *       * @param out      * @throws IOException      */
specifier|public
name|void
name|writeExternal
parameter_list|(
name|ObjectOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
operator|(
name|name
operator|!=
literal|null
condition|?
name|name
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
operator|(
name|destinationName
operator|!=
literal|null
condition|?
name|destinationName
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|coordinator
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
comment|/**      * read from a stream      *       * @param in      * @throws IOException      * @throws ClassNotFoundException      */
specifier|public
name|void
name|readExternal
parameter_list|(
name|ObjectInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|this
operator|.
name|name
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|this
operator|.
name|destinationName
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|this
operator|.
name|coordinator
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
operator|(
name|Map
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

