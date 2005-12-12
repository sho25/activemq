begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|activemq
operator|.
name|util
operator|.
name|URISupport
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|URISupportTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testEmptyCompositePath
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()/localhost?persistent=false"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCompositePath
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:(path)/path"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"path"
argument_list|,
name|data
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:path"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|data
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSimpleComposite
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:part1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testComposite
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:(part1://host,part2://(sub1://part,sube2:part))"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testParsingURI
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|source
init|=
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:61626/foo/bar?cheese=Edam&x=123"
argument_list|)
decl_stmt|;
name|Map
name|map
init|=
name|URISupport
operator|.
name|parseParamters
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Size: "
operator|+
name|map
argument_list|,
literal|2
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertMapKey
argument_list|(
name|map
argument_list|,
literal|"cheese"
argument_list|,
literal|"Edam"
argument_list|)
expr_stmt|;
name|assertMapKey
argument_list|(
name|map
argument_list|,
literal|"x"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|URI
name|result
init|=
name|URISupport
operator|.
name|removeQuery
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"result"
argument_list|,
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:61626/foo/bar"
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapKey
parameter_list|(
name|Map
name|map
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Map key: "
operator|+
name|key
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testParsingCompositeURI
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://(tcp://localhost:61616)?name=foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCheckParenthesis
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"fred:(((ddd))"
decl_stmt|;
name|assertFalse
argument_list|(
name|URISupport
operator|.
name|checkParenthesis
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|str
operator|+=
literal|")"
expr_stmt|;
name|assertTrue
argument_list|(
name|URISupport
operator|.
name|checkParenthesis
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

