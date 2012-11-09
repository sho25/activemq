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
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|LRUCacheTest
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LRUCacheTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testResize
parameter_list|()
throws|throws
name|Exception
block|{
name|LRUCache
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|underTest
init|=
operator|new
name|LRUCache
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|Long
name|count
init|=
operator|new
name|Long
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|count
operator|<
literal|27276827
condition|;
name|count
operator|++
control|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|underTest
operator|.
name|containsKey
argument_list|(
name|count
argument_list|)
condition|)
block|{
name|underTest
operator|.
name|put
argument_list|(
name|count
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|long
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|duration
operator|>
name|max
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"count: "
operator|+
name|count
operator|+
literal|", new max="
operator|+
name|duration
argument_list|)
expr_stmt|;
name|max
operator|=
name|duration
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|%
literal|100000000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"count: "
operator|+
name|count
operator|+
literal|", max="
operator|+
name|max
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"size is still in order"
argument_list|,
literal|1000
argument_list|,
name|underTest
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
