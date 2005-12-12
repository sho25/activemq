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
name|java
operator|.
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

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
comment|/**  * Implements all the default JDBC operations that are used  * by the JDBCPersistenceAdapter.  *<p/>  * Subclassing is encouraged to override the default  * implementation of methods to account for differences  * in JDBC Driver implementations.  *<p/>  * The JDBCAdapter inserts and extracts BLOB data using the  * getBytes()/setBytes() operations.  *<p/>  * The databases/JDBC drivers that use this adapter are:  *<ul>  *<li></li>  *</ul>  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|OracleJDBCAdapter
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
name|setLongDataType
argument_list|(
literal|"NUMBER"
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
name|OracleJDBCAdapter
parameter_list|()
block|{
name|this
argument_list|(
name|createStatementProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OracleJDBCAdapter
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
specifier|protected
name|byte
index|[]
name|getBinaryData
parameter_list|(
name|ResultSet
name|rs
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|SQLException
block|{
comment|// Get as a BLOB
name|Blob
name|aBlob
init|=
name|rs
operator|.
name|getBlob
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
name|aBlob
operator|.
name|getBytes
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|aBlob
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

