package usa.synergy.utilities.assets.utilities;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookBuilder {

  private final ItemStack book;
  private final BookMeta bookMeta;

  public BookBuilder(int amount) {
    this.book = new ItemStack(Material.WRITTEN_BOOK, amount);
    this.bookMeta = (BookMeta) book.getItemMeta();
  }

  public BookBuilder() {
    this.book = new ItemStack(Material.WRITTEN_BOOK, 1);
    this.bookMeta = (BookMeta) book.getItemMeta();
  }

  public BookBuilder setAuthor(String author) {
    this.bookMeta.setAuthor(author);
    return this;
  }

  public BookBuilder addPages(String... pages) {
    this.bookMeta.addPage(pages);
    return this;
  }

  public BookBuilder setPages(List<String> pages) {
    this.bookMeta.setPages(pages);
    return this;
  }

  public BookBuilder setPages(String... pages) {
    this.bookMeta.setPages(pages);
    return this;
  }

  public BookBuilder setGeneration(BookMeta.Generation generation) {
    this.bookMeta.setGeneration(generation);
    return this;
  }

  public BookBuilder setTitle(String title) {
    this.bookMeta.setTitle(title);
    return this;
  }

  public ItemStack build() {
    this.book.setItemMeta(this.bookMeta);

    return this.book;
  }
}
