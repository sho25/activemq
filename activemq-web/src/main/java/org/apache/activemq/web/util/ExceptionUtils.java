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
name|web
operator|.
name|util
package|;
end_package

begin_class
specifier|public
class|class
name|ExceptionUtils
block|{
comment|/**      * Finds the root cause of an exception.  Will return the original      * exception if the first getCause returns null.      *      * @param e      * @return      */
specifier|public
specifier|static
name|Throwable
name|getRootCause
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|Throwable
name|result
init|=
name|e
decl_stmt|;
comment|//loop over to find the root cause while guarding against cycles
while|while
condition|(
name|result
operator|!=
literal|null
operator|&&
name|result
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
name|e
operator|!=
name|result
operator|.
name|getCause
argument_list|()
operator|&&
name|result
operator|!=
name|result
operator|.
name|getCause
argument_list|()
condition|)
block|{
name|result
operator|=
name|result
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Returns true if the passed in class is the root cause of the exception      *      * @param e      * @param clazz      * @return      */
specifier|public
specifier|static
name|boolean
name|isRootCause
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|,
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|==
literal|null
operator|||
name|e
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|clazz
operator|.
name|isInstance
argument_list|(
name|getRootCause
argument_list|(
name|e
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

