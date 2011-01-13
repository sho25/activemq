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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultAuditLogFactory
implements|implements
name|AuditLogFactory
block|{
specifier|private
name|ArrayList
argument_list|<
name|AuditLog
argument_list|>
name|auditLogs
init|=
operator|new
name|ArrayList
argument_list|<
name|AuditLog
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|DefaultAuditLogFactory
parameter_list|()
block|{
name|ServiceLoader
argument_list|<
name|AuditLog
argument_list|>
name|logs
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|AuditLog
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|AuditLog
name|log
range|:
name|logs
control|)
block|{
name|auditLogs
operator|.
name|add
argument_list|(
name|log
argument_list|)
expr_stmt|;
block|}
comment|// add default audit log if non was found
if|if
condition|(
name|auditLogs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|auditLogs
operator|.
name|add
argument_list|(
operator|new
name|DefaultAuditLog
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|AuditLog
argument_list|>
name|getAuditLogs
parameter_list|()
block|{
return|return
name|auditLogs
return|;
block|}
block|}
end_class

end_unit

