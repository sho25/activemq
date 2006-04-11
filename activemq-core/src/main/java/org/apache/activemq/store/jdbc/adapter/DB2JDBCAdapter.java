begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
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

begin_comment
comment|/**  *  * @org.apache.xbean.XBean element="db2JDBCAdapter"  *  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|DB2JDBCAdapter
extends|extends
name|DefaultJDBCAdapter
block|{
specifier|public
name|DB2JDBCAdapter
parameter_list|()
block|{
name|batchStatments
operator|=
literal|false
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
name|index
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

