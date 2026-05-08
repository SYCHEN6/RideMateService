import requests
from bs4 import BeautifulSoup
import os
import time
import re
from urllib.parse import urljoin, urlparse
import random
from datetime import datetime

# 配置
CRAWLER_DELAY = random.uniform(1, 3)  # 随机爬虫延迟，避免被封
MAX_FILES = 200  # 最多保存200个文件
OUTPUT_DIR = './'  # 输出目录

topic_urls = {
    "cycling_routes_zhihu": "https://www.zhihu.com/topic/20046744/hot",  # 知乎骑行话题
    "cycling_experience_xhs": "https://www.xiaohongshu.com/search_result?keyword=%E9%A9%AC%E8%A1%8C%E8%A1%8C%E9%81%93",  # 小红书骑行路线
    "cycling_safety_weibo": "https://s.weibo.com/weibo?q=%E9%A9%AC%E8%A1%8C%E5%AE%89%E5%85%A8",  # 微博骑行安全
    "cycling_equipment_bilibili": "https://search.bilibili.com/article?keyword=%E9%A9%AC%E8%A1%8C%E8%A3%85%E5%A4%87",  # B站骑行装备
    "cycling_training_mafengwo": "https://www.mafengwo.cn/search/q.php?q=%E9%A9%AC%E8%A1%8C%E8%AE%AD%E7%BB%83",  # 马蜂窝骑行训练
}

# 已访问的URL集合，避免重复爬取
visited_urls = set()

# 已保存的文件数量
file_count = 0

# 请求头，模拟浏览器访问
headers_list = [
    {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3',
    },
    {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3',
    },
    {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.64',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3',
    }
]

# 确保输出目录存在
os.makedirs(OUTPUT_DIR, exist_ok=True)

# 提取网页内容的函数
def extract_content(soup, url):
    print(f"  开始提取内容: {url}")
    
    # 提取标题
    title = soup.find('h1') or soup.find('h2', class_='title') or soup.find('div', class_='title')
    title_text = title.text.strip() if title else "Untitled"
    print(f"  标题: {title_text}")
    
    # 根据不同网站调整内容提取策略
    content = ""
    domain = urlparse(url).netloc
    
    if 'zhihu.com' in domain:
        # 知乎内容提取
        content_div = soup.find('div', class_='ContentItem-actions')
        if not content_div:
            content_div = soup.find('div', class_='RichContent-inner')
    elif 'xiaohongshu.com' in domain:
        # 小红书内容提取
        content_div = soup.find('div', class_='content')
        if not content_div:
            content_div = soup.find('div', class_='note-content')
    elif 'weibo.com' in domain:
        # 微博内容提取
        content_div = soup.find('div', class_='content')
        if not content_div:
            content_div = soup.find('div', class_='weibo-text')
    elif 'bilibili.com' in domain:
        # B站内容提取
        content_div = soup.find('div', class_='article-holder')
        if not content_div:
            content_div = soup.find('div', class_='article-content')
    elif 'mafengwo.cn' in domain:
        # 马蜂窝内容提取
        content_div = soup.find('div', class_='content')
        if not content_div:
            content_div = soup.find('div', class_='travel-notes-content')
    else:
        # 默认提取策略
        content_div = soup.find('div', class_='article-body') or soup.find('div', class_='content') or soup.find('main')
    
    print(f"  找到内容div: {content_div is not None}")
    
    if not content_div:
        # 如果找不到正文div，尝试找到所有的p标签
        paragraphs = soup.find_all('p')
        print(f"  找到{len(paragraphs)}个段落标签")
        content = "\n".join([p.text.strip() for p in paragraphs])
    else:
        # 清理内容，移除脚本、样式和广告
        for script in content_div(['script', 'style', 'aside', 'nav', 'footer']):
            script.decompose()
        
        # 提取所有段落
        paragraphs = content_div.find_all('p')
        print(f"  找到{len(paragraphs)}个段落标签")
        
        # 提取段落内容
        for p in paragraphs:
            content += p.text.strip() + "\n\n"
        
        # 提取表格内容
        tables = content_div.find_all('table')
        if tables:
            print(f"  找到{len(tables)}个表格")
            for i, table in enumerate(tables):
                content += f"\n## 表格 {i+1}\n"
                rows = table.find_all('tr')
                for row in rows:
                    cells = row.find_all(['td', 'th'])
                    row_content = "| " + " | ".join([cell.text.strip() for cell in cells]) + " |"
                    content += row_content + "\n"
        
        # 提取图片信息（只保留图片URL）
        images = content_div.find_all('img')
        if images:
            print(f"  找到{len(images)}张图片")
            content += "\n## 图片\n"
            for i, img in enumerate(images[:5]):  # 最多保存5张图片信息
                img_url = img.get('src') or img.get('data-src') or img.get('data-original')
                if img_url:
                    if not img_url.startswith('http'):
                        img_url = urljoin(url, img_url)
                    content += f"![图片{i+1}]({img_url})\n"
    
    # 清理内容
    content = re.sub(r'\s+', ' ', content)
    content = content.strip()
    print(f"  提取内容长度: {len(content)} 字符")
    
    return {
        "title": title_text,
        "url": url,
        "content": content,
        "domain": domain
    }

# 保存内容到markdown文件
def save_to_markdown(content, topic):
    global file_count
    
    if file_count >= MAX_FILES:
        print(f"  已达到最大文件数{MAX_FILES}，停止保存")
        return False
    
    # 生成安全的文件名
    filename = re.sub(r'[^a-zA-Z0-9\-_\u4e00-\u9fa5]', '_', content['title'].lower())[:50] + '.md'
    
    # 创建主题子目录
    topic_dir = os.path.join(OUTPUT_DIR, topic)
    os.makedirs(topic_dir, exist_ok=True)
    
    # 完整文件路径
    file_path = os.path.join(topic_dir, filename)
    
    try:
        # 写入文件
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(f"# {content['title']}\n\n")
            f.write(f"**来源**: [{content['domain']}]({content['url']})\n")
            f.write(f"**爬取时间**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")
            f.write(content['content'])
        
        file_count += 1
        print(f"  ✅ 已保存: {file_path} (总数: {file_count})")
        return True
    except Exception as e:
        print(f"  ❌ 保存失败: {file_path}")
        print(f"  错误: {str(e)}")
        return False

# 爬虫主函数
def crawl(url, topic, depth=0):
    if depth > 1:  # 减少爬取深度，避免过深爬取
        return
    
    if url in visited_urls:
        return
    
    print(f"\n爬取: {url}")
    print(f"  深度: {depth}, 主题: {topic}")
    
    try:
        # 发送HTTP请求，随机选择请求头
        headers = random.choice(headers_list)
        response = requests.get(url, headers=headers, timeout=15)
        response.raise_for_status()  # 检查请求是否成功
        
        # 解析HTML
        soup = BeautifulSoup(response.content, 'html.parser')
        
        # 提取并保存内容
        content = extract_content(soup, url)
        if content['content'] and len(content['content']) > 300:  # 只保存有足够内容的页面
            if not save_to_markdown(content, topic):
                return  # 达到最大文件数，停止爬取
        
        # 标记为已访问
        visited_urls.add(url)
        
        # 查找相关链接并继续爬取
        if depth < 1 and file_count < MAX_FILES:
            links = soup.find_all('a', href=True)
            print(f"  找到 {len(links)} 个链接")
            
            # 过滤相关链接
            relevant_links = []
            for link in links:
                href = link['href']
                full_url = urljoin(url, href)
                
                # 确保链接属于同一网站
                if urlparse(full_url).netloc == urlparse(url).netloc:
                    # 确保链接与骑行相关
                    if any(keyword in full_url.lower() for keyword in ['ride', 'cycling', 'bicycle', 'bike', '骑行', '单车', '自行车']):
                        # 避免爬取无关页面
                        if not any(exclude in full_url.lower() for exclude in ['login', 'register', 'advertise', 'contact', 'privacy', 'terms', 'user', 'profile']):
                            relevant_links.append(full_url)
            
            print(f"  筛选后剩余 {len(relevant_links)} 个相关链接")
            
            # 随机选择最多10个链接继续爬取
            for link in random.sample(relevant_links, min(10, len(relevant_links))):
                if file_count >= MAX_FILES:
                    return  # 达到最大文件数，停止爬取
                
                time.sleep(CRAWLER_DELAY)
                crawl(link, topic, depth + 1)
    
    except requests.exceptions.HTTPError as e:
        print(f"  HTTP错误: {url} - {str(e)}")
    except requests.exceptions.ConnectionError as e:
        print(f"  连接错误: {url} - {str(e)}")
    except requests.exceptions.Timeout as e:
        print(f"  超时错误: {url} - {str(e)}")
    except Exception as e:
        print(f"  爬取失败: {url}")
        print(f"  错误: {str(e)}")

# 主程序
if __name__ == "__main__":
    print(f"开始爬取骑行知识...")
    print(f"输出目录: {os.path.abspath(OUTPUT_DIR)}")
    print(f"最大文件数: {MAX_FILES}")
    print(f"爬虫延迟: {CRAWLER_DELAY:.1f}-{CRAWLER_DELAY*3:.1f}秒")
    print("=" * 60)
    
    for topic, url in topic_urls.items():
        if file_count >= MAX_FILES:
            print(f"\n已达到最大文件数{MAX_FILES}，停止爬取")
            break
            
        print(f"\n开始爬取主题: {topic}")
        print(f"起始URL: {url}")
        crawl(url, topic)
    
    print(f"\n" + "=" * 60)
    print(f"爬取完成！")
    print(f"统计信息:")
    print(f"   - 已爬取 {len(visited_urls)} 个网页")
    print(f"   - 已保存 {file_count} 个文件")
    print(f"   - 内容已保存到: {os.path.abspath(OUTPUT_DIR)}")
    print("=" * 60)