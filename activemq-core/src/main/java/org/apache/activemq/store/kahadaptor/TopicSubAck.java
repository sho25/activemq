begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|kahadaptor
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
name|kaha
operator|.
name|StoreEntry
import|;
end_import

begin_comment
comment|/**  * Holds information for location of message  *   * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|TopicSubAck
block|{
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|private
name|StoreEntry
name|storeEntry
decl_stmt|;
comment|/**      * @return the count      */
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|count
return|;
block|}
comment|/**      * @param count the count to set      */
specifier|public
name|void
name|setCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
block|}
comment|/**      * @return the value of the count after it's decremented      */
specifier|public
name|int
name|decrementCount
parameter_list|()
block|{
return|return
operator|--
name|count
return|;
block|}
comment|/**      * @return the value of the count after it's incremented      */
specifier|public
name|int
name|incrementCount
parameter_list|()
block|{
return|return
operator|++
name|count
return|;
block|}
comment|/**      * @return the storeEntry      */
specifier|public
name|StoreEntry
name|getStoreEntry
parameter_list|()
block|{
return|return
name|this
operator|.
name|storeEntry
return|;
block|}
comment|/**      * @param storeEntry the storeEntry to set      */
specifier|public
name|void
name|setStoreEntry
parameter_list|(
name|StoreEntry
name|storeEntry
parameter_list|)
block|{
name|this
operator|.
name|storeEntry
operator|=
name|storeEntry
expr_stmt|;
block|}
block|}
end_class

end_unit

