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
name|broker
operator|.
name|util
operator|.
name|AuditLogEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
specifier|public
class|class
name|HttpAuditLogEntry
extends|extends
name|AuditLogEntry
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|formattedParams
init|=
literal|""
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|params
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
operator|)
name|parameters
operator|.
name|get
argument_list|(
literal|"params"
argument_list|)
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|paramName
range|:
name|params
operator|.
name|keySet
argument_list|()
control|)
block|{
name|formattedParams
operator|+=
name|paramName
operator|+
literal|"='"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|params
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
argument_list|)
operator|+
literal|"' "
expr_stmt|;
block|}
block|}
return|return
name|user
operator|+
literal|" requested "
operator|+
name|operation
operator|+
literal|" ["
operator|+
name|formattedParams
operator|+
literal|"] from  "
operator|+
name|remoteAddr
operator|+
literal|" at "
operator|+
name|getFormattedTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit

