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
name|pool
package|;
end_package

begin_comment
comment|/**  * A cache key for the connection details  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionKey
block|{
specifier|private
specifier|final
name|String
name|userName
decl_stmt|;
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
specifier|private
name|int
name|hash
decl_stmt|;
specifier|public
name|ConnectionKey
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
name|hash
operator|=
literal|31
expr_stmt|;
if|if
condition|(
name|userName
operator|!=
literal|null
condition|)
block|{
name|hash
operator|+=
name|userName
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
name|hash
operator|*=
literal|31
expr_stmt|;
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
name|hash
operator|+=
name|password
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hash
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|that
operator|instanceof
name|ConnectionKey
condition|)
block|{
return|return
name|equals
argument_list|(
operator|(
name|ConnectionKey
operator|)
name|that
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|ConnectionKey
name|that
parameter_list|)
block|{
return|return
name|isEqual
argument_list|(
name|this
operator|.
name|userName
argument_list|,
name|that
operator|.
name|userName
argument_list|)
operator|&&
name|isEqual
argument_list|(
name|this
operator|.
name|password
argument_list|,
name|that
operator|.
name|password
argument_list|)
return|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isEqual
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|==
name|o2
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|o1
operator|!=
literal|null
operator|&&
name|o2
operator|!=
literal|null
operator|&&
name|o1
operator|.
name|equals
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

