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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|KahaStore
import|;
end_import

begin_comment
comment|/**  * Factory for creating stores  *   *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|StoreFactory
block|{
specifier|private
name|StoreFactory
parameter_list|()
block|{     }
comment|/**      * open or create a Store      *       * @param name      * @param mode      * @return the opened/created store      * @throws IOException      */
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
name|KahaStore
argument_list|(
name|name
argument_list|,
name|mode
argument_list|,
operator|new
name|AtomicLong
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Open or create a Store      *       * @param directory      * @param mode      * @return      * @throws IOException      */
specifier|public
specifier|static
name|Store
name|open
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|mode
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|KahaStore
argument_list|(
name|directory
argument_list|,
name|mode
argument_list|,
operator|new
name|AtomicLong
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * open or create a Store      * @param name      * @param mode      * @param size      * @return the opened/created store      * @throws IOException      */
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
parameter_list|,
name|AtomicLong
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|KahaStore
argument_list|(
name|name
argument_list|,
name|mode
argument_list|,
name|size
argument_list|)
return|;
block|}
comment|/**      * Open or create a Store      *       * @param directory      * @param mode      * @param size      * @return      * @throws IOException      */
specifier|public
specifier|static
name|Store
name|open
parameter_list|(
name|File
name|directory
parameter_list|,
name|String
name|mode
parameter_list|,
name|AtomicLong
name|size
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|KahaStore
argument_list|(
name|directory
argument_list|,
name|mode
argument_list|,
name|size
argument_list|)
return|;
block|}
comment|/**      * Delete a database      *       * @param name of the database      * @return true if successful      * @throws IOException      */
specifier|public
specifier|static
name|boolean
name|delete
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaStore
name|store
init|=
operator|new
name|KahaStore
argument_list|(
name|name
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
return|return
name|store
operator|.
name|delete
argument_list|()
return|;
block|}
comment|/**      * Delete a database      *       * @param directory      * @return true if successful      * @throws IOException      */
specifier|public
specifier|static
name|boolean
name|delete
parameter_list|(
name|File
name|directory
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaStore
name|store
init|=
operator|new
name|KahaStore
argument_list|(
name|directory
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
return|return
name|store
operator|.
name|delete
argument_list|()
return|;
block|}
block|}
end_class

end_unit
