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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|activemq
operator|.
name|broker
operator|.
name|jmx
operator|.
name|Sensitive
import|;
end_import

begin_class
specifier|public
class|class
name|AuditLogEntry
block|{
specifier|protected
name|String
name|user
init|=
literal|"anonymous"
decl_stmt|;
specifier|protected
name|long
name|timestamp
decl_stmt|;
specifier|protected
name|String
name|operation
decl_stmt|;
specifier|protected
name|String
name|remoteAddr
decl_stmt|;
name|SimpleDateFormat
name|formatter
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd-MM-yyyy HH:mm:ss,SSS"
argument_list|)
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|parameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
specifier|public
name|String
name|getFormattedTime
parameter_list|()
block|{
return|return
name|formatter
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|String
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
specifier|public
name|void
name|setOperation
parameter_list|(
name|String
name|operation
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
block|}
specifier|public
name|String
name|getRemoteAddr
parameter_list|()
block|{
return|return
name|remoteAddr
return|;
block|}
specifier|public
name|void
name|setRemoteAddr
parameter_list|(
name|String
name|remoteAddr
parameter_list|)
block|{
name|this
operator|.
name|remoteAddr
operator|=
name|remoteAddr
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
specifier|public
name|void
name|setParameters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
block|}
comment|/**     * Method to remove any sensitive parameters before logging.  Replaces any sensitive value with ****.  Sensitive     * values are defined on MBean interface implementation method parameters using the @Sensitive annotation.     *     * @param arguments A array of arguments to test against method signature     * @param method The method to test the arguments against.     */
specifier|public
specifier|static
name|Object
index|[]
name|sanitizeArguments
parameter_list|(
name|Object
index|[]
name|arguments
parameter_list|,
name|Method
name|method
parameter_list|)
block|{
name|Object
index|[]
name|sanitizedArguments
init|=
name|arguments
operator|.
name|clone
argument_list|()
decl_stmt|;
name|Annotation
index|[]
index|[]
name|parameterAnnotations
init|=
name|method
operator|.
name|getParameterAnnotations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|arguments
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|Annotation
name|annotation
range|:
name|parameterAnnotations
index|[
name|i
index|]
control|)
block|{
if|if
condition|(
name|annotation
operator|instanceof
name|Sensitive
condition|)
block|{
name|sanitizedArguments
index|[
name|i
index|]
operator|=
literal|"****"
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|sanitizedArguments
return|;
block|}
block|}
end_class

end_unit

