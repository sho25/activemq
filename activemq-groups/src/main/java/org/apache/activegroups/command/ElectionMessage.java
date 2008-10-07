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
name|activegroups
operator|.
name|command
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
name|org
operator|.
name|apache
operator|.
name|activegroups
operator|.
name|Member
import|;
end_import

begin_comment
comment|/**  * Used to pass information around  *  */
end_comment

begin_class
specifier|public
class|class
name|ElectionMessage
implements|implements
name|Externalizable
block|{
specifier|public
specifier|static
enum|enum
name|MessageType
block|{
name|ELECTION
block|,
name|ANSWER
block|,
name|COORDINATOR
block|}
empty_stmt|;
specifier|private
name|Member
name|member
decl_stmt|;
specifier|private
name|MessageType
name|type
decl_stmt|;
comment|/**      * @return the member      */
specifier|public
name|Member
name|getMember
parameter_list|()
block|{
return|return
name|this
operator|.
name|member
return|;
block|}
comment|/**      * @param member the member to set      */
specifier|public
name|void
name|setMember
parameter_list|(
name|Member
name|member
parameter_list|)
block|{
name|this
operator|.
name|member
operator|=
name|member
expr_stmt|;
block|}
comment|/**      * @return the type      */
specifier|public
name|MessageType
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
comment|/**      * @param type the type to set      */
specifier|public
name|void
name|setType
parameter_list|(
name|MessageType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * @return true if election message      */
specifier|public
name|boolean
name|isElection
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
operator|!=
literal|null
operator|&&
name|this
operator|.
name|type
operator|.
name|equals
argument_list|(
name|MessageType
operator|.
name|ELECTION
argument_list|)
return|;
block|}
comment|/**      * @return true if answer message      */
specifier|public
name|boolean
name|isAnswer
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
operator|!=
literal|null
operator|&&
name|this
operator|.
name|type
operator|.
name|equals
argument_list|(
name|MessageType
operator|.
name|ANSWER
argument_list|)
return|;
block|}
comment|/**      * @return true if coordinator message      */
specifier|public
name|boolean
name|isCoordinator
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
operator|!=
literal|null
operator|&&
name|this
operator|.
name|type
operator|.
name|equals
argument_list|(
name|MessageType
operator|.
name|COORDINATOR
argument_list|)
return|;
block|}
specifier|public
name|ElectionMessage
name|copy
parameter_list|()
block|{
name|ElectionMessage
name|result
init|=
operator|new
name|ElectionMessage
argument_list|()
decl_stmt|;
name|result
operator|.
name|member
operator|=
name|this
operator|.
name|member
expr_stmt|;
name|result
operator|.
name|type
operator|=
name|this
operator|.
name|type
expr_stmt|;
return|return
name|result
return|;
block|}
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
name|member
operator|=
operator|(
name|Member
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|this
operator|.
name|type
operator|=
operator|(
name|MessageType
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
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
name|writeObject
argument_list|(
name|this
operator|.
name|member
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|this
operator|.
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ElectionMessage: "
operator|+
name|this
operator|.
name|member
operator|+
literal|"{"
operator|+
name|this
operator|.
name|type
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

