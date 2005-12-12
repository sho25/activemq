begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|store
operator|.
name|jdbc
operator|.
name|adapter
package|;
end_package

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|store
operator|.
name|jdbc
operator|.
name|StatementProvider
import|;
end_import

begin_comment
comment|/**  * Provides JDBCAdapter since that uses  * IMAGE datatype to hold binary data.  *   * The databases/JDBC drivers that use this adapter are:  *<ul>  *<li>Sybase</li>  *<li>MS SQL</li>  *</ul>  *   */
end_comment

begin_class
specifier|public
class|class
name|ImageBasedJDBCAdaptor
extends|extends
name|DefaultJDBCAdapter
block|{
specifier|public
specifier|static
name|StatementProvider
name|createStatementProvider
parameter_list|()
block|{
name|DefaultStatementProvider
name|answer
init|=
operator|new
name|DefaultStatementProvider
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setBinaryDataType
argument_list|(
literal|"IMAGE"
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|ImageBasedJDBCAdaptor
parameter_list|()
block|{
name|super
argument_list|(
name|createStatementProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ImageBasedJDBCAdaptor
parameter_list|(
name|StatementProvider
name|provider
parameter_list|)
block|{
name|super
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

