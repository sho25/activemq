begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|File
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|StoreImpl
import|;
end_import

begin_comment
comment|/**  * Factory for creating stores  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|StoreFactory
block|{
comment|/**      * open or create a Store      * @param name      * @param mode      * @return the opened/created store      * @throws IOException      */
specifier|public
specifier|static
name|Store
name|open
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StoreImpl
argument_list|(
name|name
argument_list|,
name|mode
argument_list|)
return|;
block|}
comment|/**      * Delete a database      * @param name of the database      * @return true if successful      */
specifier|public
specifier|static
name|boolean
name|delete
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|delete
argument_list|()
return|;
block|}
block|}
end_class

end_unit

