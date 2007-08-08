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

begin_comment
comment|/**  * Used by RootContainers  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ContainerId
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
literal|8883779541021821943L
decl_stmt|;
specifier|private
name|Object
name|key
decl_stmt|;
specifier|private
name|String
name|dataContainerName
decl_stmt|;
specifier|public
name|ContainerId
parameter_list|()
block|{     }
specifier|public
name|ContainerId
parameter_list|(
name|Object
name|key
parameter_list|,
name|String
name|dataContainerName
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|dataContainerName
operator|=
name|dataContainerName
expr_stmt|;
block|}
comment|/**      * @return Returns the dataContainerPrefix.      */
specifier|public
name|String
name|getDataContainerName
parameter_list|()
block|{
return|return
name|dataContainerName
return|;
block|}
comment|/**      * @param dataContainerName The dataContainerPrefix to set.      */
specifier|public
name|void
name|setDataContainerName
parameter_list|(
name|String
name|dataContainerName
parameter_list|)
block|{
name|this
operator|.
name|dataContainerName
operator|=
name|dataContainerName
expr_stmt|;
block|}
comment|/**      * @return Returns the key.      */
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
comment|/**      * @param key The key to set.      */
specifier|public
name|void
name|setKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|key
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
name|obj
operator|instanceof
name|ContainerId
condition|)
block|{
name|ContainerId
name|other
init|=
operator|(
name|ContainerId
operator|)
name|obj
decl_stmt|;
name|result
operator|=
name|other
operator|.
name|key
operator|.
name|equals
argument_list|(
name|this
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
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
name|writeUTF
argument_list|(
name|getDataContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeObject
argument_list|(
name|key
argument_list|)
expr_stmt|;
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
name|dataContainerName
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|key
operator|=
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CID{"
operator|+
name|dataContainerName
operator|+
literal|":"
operator|+
name|key
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

